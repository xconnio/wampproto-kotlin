package io.xconn.wampproto.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.xconn.cryptology.Util.generateRandomBytesArray
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CRAAuthenticator(
    override val authID: String,
    override val authExtra: Map<String, Any> = emptyMap(),
    private val secret: String,
) : ClientAuthenticator {
    companion object {
        const val TYPE = "wampcra"
        const val DEFAULT_ITERATIONS = 1000
        const val DEFAULT_KEY_LENGTH = 256
    }

    override val authMethod: String = TYPE

    override fun authenticate(challenge: Challenge): Authenticate {
        val challengeHex =
            challenge.extra["challenge"] as? String
                ?: throw IllegalArgumentException("challenge string missing in extra")

        val salt = challenge.extra["salt"] as? String
        var rawSecret: ByteArray
        if (salt.isNullOrEmpty()) {
            rawSecret = secret.toByteArray(Charsets.UTF_8)
        } else {
            val iterations =
                (challenge.extra["iterations"] as? Int)?.toInt()
                    ?: throw IllegalArgumentException("Iterations missing in extra")
            val keylen =
                (challenge.extra["keylen"] as? Int)?.toInt()
                    ?: throw IllegalArgumentException("Key length missing in extra")

            rawSecret = deriveCRAKey(salt, secret, iterations, keylen)
        }

        val signed = signWampCRAChallenge(challengeHex, rawSecret)

        return Authenticate(signed, emptyMap())
    }
}

internal fun deriveCRAKey(saltStr: String, secret: String, iterations: Int, keyLength: Int): ByteArray {
    val salt = saltStr.toByteArray(Charsets.UTF_8)

    val effectiveIterations = if (iterations == 0) CRAAuthenticator.DEFAULT_ITERATIONS else iterations
    val effectiveKeyLength = if (keyLength == 0) CRAAuthenticator.DEFAULT_KEY_LENGTH else keyLength * 8

    val keySpec = PBEKeySpec(secret.toCharArray(), salt, effectiveIterations, effectiveKeyLength)
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val derivedKeyBytes = keyFactory.generateSecret(keySpec).encoded

    return Base64.getEncoder().encode(derivedKeyBytes)
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
