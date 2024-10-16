package io.xconn.wampproto.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.xconn.cryptology.Util.generateRandomBytesArray
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CRAAuthenticator(
    override val authID: String,
    override val authExtra: Map<String, Any> = emptyMap(),
    private val secret: String,
) : ClientAuthenticator {
    companion object {
        const val TYPE = "wampcra"
    }

    override val authMethod: String = TYPE

    override fun authenticate(challenge: Challenge): Authenticate {
        val challengeHex =
            challenge.extra["challenge"] as? String
                ?: throw IllegalArgumentException("challenge string missing in extra")

        val signed = signWampCRAChallenge(challengeHex, secret.toByteArray(Charsets.UTF_8))

        return Authenticate(signed, emptyMap())
    }
}

private fun utcNow(): String {
    val dt = java.time.Instant.now()

    return dt.toString().substring(0, 23) + "Z"
}

fun generateWAMPCRAChallenge(sessionID: Int, authid: String, authRole: String, provider: String): String {
    val nonceRaw = generateRandomBytesArray(16)
    val nonce = nonceRaw.toHexString()

    val data: Map<String, Any> =
        mapOf(
            "nonce" to nonce,
            "authprovider" to provider,
            "authid" to authid,
            "authrole" to authRole,
            "authmethod" to CRAAuthenticator.TYPE,
            "session" to sessionID,
            "timestamp" to utcNow(),
        )

    return ObjectMapper().writeValueAsString(data)
}

fun signWampCRAChallenge(challenge: String, key: ByteArray): String {
    val secretKey = SecretKeySpec(key, "HmacSHA256")

    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKey)
    val signature = mac.doFinal(challenge.toByteArray(Charsets.UTF_8))

    return Base64.getEncoder().encodeToString(signature)
}

fun verifyWampCRASignature(signature: String, challenge: String, key: ByteArray): Boolean {
    val localSignature = signWampCRAChallenge(challenge, key)

    return signature == localSignature
}
