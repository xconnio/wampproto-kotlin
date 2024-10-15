package io.xconn.wampproto.auth

import io.xconn.cryptology.CryptoSign
import io.xconn.cryptology.CryptoSign.getPublicKey
import io.xconn.cryptology.KeyPair
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge
import java.security.SecureRandom

class CryptoSignAuthenticator(
    override val authID: String,
    private val privateKey: String,
    override val authExtra: MutableMap<String, Any> = mutableMapOf(),
) : ClientAuthenticator {
    companion object {
        const val TYPE = "cryptosign"
    }

    init {
        if (!authExtra.containsKey("pubkey")) {
            val publicKeyBytes = getPublicKey(privateKey.hexStringToByteArray())
            authExtra["pubkey"] = publicKeyBytes.toHexString()
        }
    }

    override val authMethod: String = TYPE

    override fun authenticate(challenge: Challenge): Authenticate {
        val challengeHex =
            challenge.extra["challenge"] as? String
                ?: throw IllegalArgumentException("challenge string missing in extra")

        val signed = signCryptoSignChallenge(challengeHex, privateKey)

        return Authenticate(signed + challengeHex, emptyMap())
    }
}

fun generateCryptoSignChallenge(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)

    return bytes.toHexString()
}

fun signCryptoSignChallenge(challenge: String, privateKey: String): String {
    val signedChallenge = CryptoSign.sign(privateKey.hexStringToByteArray(), challenge.hexStringToByteArray())

    return signedChallenge.toHexString()
}

fun verifyCryptoSignSignature(signature: String, publicKey: ByteArray): Boolean {
    val sig = signature.substring(0, 128)
    val challenge = signature.substring(128, signature.length)

    return CryptoSign.verify(publicKey, challenge.hexStringToByteArray(), sig.hexStringToByteArray())
}

fun generateCryptoSignKeyPair(): KeyPair? {
    return CryptoSign.generateKeyPair()
}

fun String.hexStringToByteArray(): ByteArray {
    return this.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun ByteArray.toHexString(): String {
    return this.joinToString("") { "%02x".format(it) }
}
