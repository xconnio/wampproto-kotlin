package io.xconn.wampproto.interoperability.auth

import io.xconn.wampproto.auth.generateCryptoSignChallenge
import io.xconn.wampproto.auth.hexStringToByteArray
import io.xconn.wampproto.auth.signCryptoSignChallenge
import io.xconn.wampproto.auth.verifyCryptoSignSignature
import io.xconn.wampproto.interoperability.runCommand
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoSignAuthenticatorTest {
    private val testPublicKey = "2b7ec216daa877c7f4c9439db8a722ea2340eacad506988db2564e258284f895"
    private val testPrivateKey = "022b089bed5ab78808365e82dd12c796c835aeb98b4a5a9e099d3e72cb719516"

    @Test
    fun testGenerateChallenge() {
        val challenge = generateCryptoSignChallenge()

        val signature = runCommand("wampproto auth cryptosign sign-challenge $challenge $testPrivateKey")

        val isVerified = runCommand("wampproto auth cryptosign verify-signature ${signature.trim()} $testPublicKey")
        assertEquals("Signature verified successfully", isVerified)
    }

    @Test
    fun testSignCryptoSignChallenge() {
        val challenge = runCommand("wampproto auth cryptosign generate-challenge")

        var signature = signCryptoSignChallenge(challenge.trim(), testPrivateKey)

        if (signature.hexStringToByteArray().size == 64) {
            signature += challenge.trim()
        }

        val isVerified = runCommand("wampproto auth cryptosign verify-signature $signature $testPublicKey")
        assertEquals("Signature verified successfully", isVerified)
    }

    @Test
    fun testVerifyCryptoSignSignature() {
        val challenge = runCommand("wampproto auth cryptosign generate-challenge")

        val signature = runCommand("wampproto auth cryptosign sign-challenge ${challenge.trim()} $testPrivateKey")

        val isVerified = verifyCryptoSignSignature(signature.trim(), testPublicKey.hexStringToByteArray())
        assertEquals(true, isVerified)
    }
}
