package io.xconn.auth

import io.xconn.cryptology.CryptoSign
import io.xconn.cryptology.KeyPair
import java.security.SecureRandom

class CryptoSign {
    companion object {
        @ExperimentalStdlibApi
        fun generateChallenge(): String {
            val random = SecureRandom()
            val bytes = ByteArray(32)
            random.nextBytes(bytes)

            return bytes.toHexString()
        }

        @ExperimentalStdlibApi
        fun signChallenge(
            challenge: String,
            privateKey: String,
        ): ByteArray {
            return CryptoSign.sign(privateKey.hexToByteArray(), challenge.hexToByteArray())
        }

        fun verifySignature(
            signature: String,
            publicKey: ByteArray,
        ): Boolean {
            TODO("Not Implemented.")
        }

        fun generateKeyPair(): KeyPair? {
            return CryptoSign.generateKeyPair()
        }
    }
}
