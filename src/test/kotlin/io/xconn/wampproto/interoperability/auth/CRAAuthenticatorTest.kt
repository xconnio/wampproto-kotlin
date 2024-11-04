package io.xconn.wampproto.interoperability.auth

import io.xconn.wampproto.auth.CRAAuthenticator
import io.xconn.wampproto.auth.generateWAMPCRAChallenge
import io.xconn.wampproto.auth.signWampCRAChallenge
import io.xconn.wampproto.auth.verifyWampCRASignature
import io.xconn.wampproto.interoperability.runCommand
import io.xconn.wampproto.messages.Challenge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.trim

class CRAAuthenticatorTest {
    private val sessionID = 123
    private val authID = "foo"
    private val authRole = "admin"
    private val provider = "provider"
    private val testSecret = "secret"
    private val salt = "salt"
    private val keylength = 32
    private val iterations = 1000
    private val craChallenge =
        """{"nonce":"cdcb3b12d56e12825be99f38f55ba43f","authprovider":"provider",""" +
            """"authid":"foo","authrole":"admin","authmethod":"wampcra","session":123,"timestamp":"2024-05-07T09:25:13.307Z"}"""
    private val authExtra = mapOf("challenge" to craChallenge, "salt" to salt, "iterations" to iterations, "keylen" to keylength)

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

    @Test
    fun testSignWAMPCRASignatureWithSalt() {
        val challenge = Challenge(CRAAuthenticator.TYPE, authExtra)
        val authenticator = CRAAuthenticator(authID, authExtra, testSecret)
        val authenticate = authenticator.authenticate(challenge)

        val saltSecret = runCommand("wampproto auth cra derive-key $salt $testSecret -i $iterations -l $keylength")

        runCommand("wampproto auth cra verify-signature $craChallenge ${authenticate.signature} $saltSecret")
    }

    @Test
    fun testVerifyWAMPCRASignatureWithSalt() {
        val challenge = runCommand("wampproto auth cra generate-challenge $sessionID $authID $authRole $provider")

        val saltSecret = runCommand("wampproto auth cra derive-key $salt $testSecret -i $iterations -l $keylength")
        val signature = runCommand("wampproto auth cra sign-challenge ${challenge.trim()} ${saltSecret.trim()}")

        val isVerified = verifyWampCRASignature(signature.trim(), challenge.trim(), saltSecret.trim().encodeToByteArray())
        assertEquals(true, isVerified)
    }
}
