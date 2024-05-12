package io.xconn.auth

import com.fasterxml.jackson.databind.ObjectMapper
import java.security.SecureRandom

class CRA {
    companion object {
        @ExperimentalStdlibApi
        fun generateChallenge(
            sessionID: Int,
            authid: String,
            authRole: String,
            provider: String,
        ): String {
            val nonceRaw = ByteArray(16)
            SecureRandom().nextBytes(nonceRaw)
            val nonce = nonceRaw.toHexString()

            val data: HashMap<String, Any> =
                hashMapOf(
                    "nonce" to nonce,
                    "authprovider" to provider,
                    "authid" to authid,
                    "authrole" to authRole,
                    "authmethod" to "wampcra",
                    "session" to sessionID,
                    "timestamp" to "FIXME",
                )

            return ObjectMapper().writeValueAsString(data)
        }

        fun signChallenge(
            challenge: String,
            key: ByteArray,
        ): String {
            TODO()
        }

        fun verifySignature(
            signature: String,
            challenge: String,
            key: ByteArray,
        ): Boolean {
            TODO()
        }
    }
}
