package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Challenge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CryptoSignAuthenticatorTest {
    private val authID = "authID"
    private val privateKeyHex = "c7e8c1f8f16ec37f53ed153f8afb7f18469b051f1d24dbea2097a2a104b2e9db"
    private val publicKeyHex = "c53e4f2756a52ca1ed5cd00da108b3ed7bcffe6294e78283521e5102824f52d3"

    private val challenge = "a1d483092ec08960fedbaed2bc1d411568a59077b794210e251bd3abb1563f7c"
    private val signature =
        "01d4b7a515b1023196e2bbb57c5202da72088f99a17eaeed62ba97ebf93381b92a3e843" +
            "0154667e194d971fb41b090a9338b92021c39271e910a8ea072fe950c"

    private val authenticator = CryptoSignAuthenticator(authID, privateKeyHex, mutableMapOf())

    @Test
    fun testConstructor() {
        assertNotNull(authenticator)
        assertEquals(authID, authenticator.authID)
        assertEquals(mutableMapOf("pubkey" to publicKeyHex), authenticator.authExtra)
        assertEquals("cryptosign", authenticator.authMethod)
    }

    @Test
    fun testAuthenticate() {
        val authenticate = authenticator.authenticate(Challenge("cryptosign", mapOf("challenge" to challenge)))
        assertEquals(authenticate.signature, signature + challenge)
    }

    @Test
    fun testGenerateCryptoSignChallenge() {
        val challenge = generateCryptoSignChallenge()
        assertEquals(64, challenge.length)
    }

    @Test
    fun testSignCryptoSignChallenge() {
        val sig = signCryptoSignChallenge(challenge, privateKeyHex)
        assertEquals(signature, sig)
    }

    @Test
    fun testVerifyCryptoSignSignature() {
        val isVerified = verifyCryptoSignSignature(signature + challenge, publicKeyHex.hexStringToByteArray())
        assertTrue(isVerified)
    }
}
