package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Challenge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CRAAuthenticatorTest {
    private val sessionID = 123
    private val authID = "authID"
    private val authRole = "admin"
    private val provider = "provider"
    private val authExtra = mapOf("challenge" to "data")
    private val key = "6d9b906ad60d1f4dd796dbadcc2e2252310565ccdc6fe10b289df5684faf2a46"
    private val authenticator = CRAAuthenticator(authID, authExtra, key)
    private val validSignature = "DIVL3bKs/Ei91eQyYznzUqEsiTmX705BNEXuicNpi8A="
    private val craChallenge =
        """{"nonce":"cdcb3b12d56e12825be99f38f55ba43f","authprovider":"provider",""" +
            """"authid":"foo","authrole":"admin","authmethod":"wampcra","session":123,"timestamp":"2024-05-07T09:25:13.307Z"}"""

    @Test
    fun constructor() {
        assertNotNull(authenticator)
        assertEquals(authID, authenticator.authID)
        assertEquals(authExtra, authenticator.authExtra)
        assertEquals("wampcra", authenticator.authMethod)
    }

    @Test
    fun testAuthenticate() {
        val challenge = Challenge(authenticator.authMethod, mapOf("challenge" to craChallenge))
        val authenticate = authenticator.authenticate(challenge)
        assertEquals(authenticate.signature, validSignature)
    }

    @Test
    fun testGenerateWAMPCRAChallenge() {
        val challenge = generateWAMPCRAChallenge(sessionID, authID, authRole, provider)
        require(challenge.isNotEmpty())
    }

    @Test
    fun testSignWampCRAChallenge() {
        val signature = signWampCRAChallenge(craChallenge, key.toByteArray(Charsets.UTF_8))
        require(signature.isNotEmpty())
    }

    @Test
    fun testVerifyWampCRASignature() {
        val isVerified = verifyWampCRASignature(validSignature, craChallenge, key.toByteArray(Charsets.UTF_8))
        assertTrue(isVerified)
    }

    @Test
    fun testVerifyWampCRASignatureIncorrect() {
        val badSignature = "bad signature"
        val isVerified = verifyWampCRASignature(badSignature, craChallenge, key.toByteArray(Charsets.UTF_8))
        assert(!isVerified)
    }
}
