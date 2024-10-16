package io.xconn.wampproto.interoperability.auth

import io.xconn.wampproto.auth.generateWAMPCRAChallenge
import io.xconn.wampproto.auth.signWampCRAChallenge
import io.xconn.wampproto.auth.verifyWampCRASignature
import io.xconn.wampproto.interoperability.runCommand
import kotlin.test.Test
import kotlin.test.assertEquals

class CRAAuthenticatorTest {
    private val sessionID = 123
    private val authID = "john"
    private val authRole = "admin"
    private val provider = "provider"
    private val testSecret = "secret"

    @Test
    fun testGenerateChallenge() {
        val challenge = generateWAMPCRAChallenge(sessionID, authID, authRole, provider)

        val signature = runCommand("wampproto auth cra sign-challenge $challenge $testSecret")

        runCommand("wampproto auth cra verify-signature $challenge ${signature.trim()} $testSecret")
    }

    @Test
    fun testSignWAMPCRAChallenge() {
        val challenge = runCommand("wampproto auth cra generate-challenge $sessionID $authID $authRole $provider")

        val signature = signWampCRAChallenge(challenge.trim(), testSecret.toByteArray(Charsets.UTF_8))

        runCommand("wampproto auth cra verify-signature $challenge $signature $testSecret")
    }

    @Test
    fun testVerifyWAMPCRASignature() {
        val challenge = runCommand("wampproto auth cra generate-challenge $sessionID $authID $authRole $provider")

        val signature = runCommand("wampproto auth cra sign-challenge ${challenge.trim()} $testSecret")

        val isVerified = verifyWampCRASignature(signature.trim(), challenge.trim(), testSecret.toByteArray(Charsets.UTF_8))
        assertEquals(true, isVerified)
    }
}
