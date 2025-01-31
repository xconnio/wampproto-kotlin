package io.xconn.wampproto

import io.xconn.wampproto.auth.TicketAuthenticator
import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge
import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.messages.Welcome
import io.xconn.wampproto.serializers.JSONSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JoinerTest {
    private val testRealm = "test.realm"
    private val testSessionID = 12345L
    private val testAuthID = "test_authid"
    private val testAuthRole = "test_role"
    private val testAuthMethod = "anonymous"

    @Test
    fun sendHello() {
        val joiner = Joiner(testRealm)
        val serializedHello = joiner.sendHello()
        assertEquals(
            """[1,"test.realm",{"authmethods":["anonymous"],"roles":{"caller":{"features":{}},""" +
                """"callee":{"features":{}},"publisher":{"features":{}},"subscriber":{"features":{}}}}]""",
            serializedHello,
        )

        val deserializedHello = JSONSerializer().deserialize(serializedHello)
        assertTrue(deserializedHello is Hello)

        val helloMessage = deserializedHello as Hello
        assertEquals(testRealm, helloMessage.realm)
        assertEquals(testAuthMethod, helloMessage.authMethods[0])
        assertEquals("", helloMessage.authID)
        assertEquals(clientRoles, helloMessage.roles)
    }

    @Test
    fun receiveWelcomeMessage() {
        val joiner = Joiner(testRealm)
        joiner.sendHello()

        val welcomeMessage = Welcome(testSessionID, clientRoles, testAuthID, testAuthRole, testAuthMethod)
        val serializedWelcome = JSONSerializer().serialize(welcomeMessage)

        val result = joiner.receive(serializedWelcome)
        assertNull(result) // No further message expected after Welcome

        val sessionDetails = joiner.getSessionDetails()
        assertEquals(testSessionID, sessionDetails.sessionID)
        assertEquals(testRealm, sessionDetails.realm)
        assertEquals(testAuthID, sessionDetails.authid)
        assertEquals(testAuthRole, sessionDetails.authrole)
    }

    @Test
    fun receiveChallengeMessage() {
        val joiner = Joiner(testRealm, authenticator = TicketAuthenticator(testAuthID, emptyMap(), "test"))
        joiner.sendHello()

        val challengeMessage = Challenge("cryptosign", mapOf("challenge" to "123456"))
        val serializedChallenge = JSONSerializer().serialize(challengeMessage)

        val result = joiner.receive(serializedChallenge)
        assertNotNull(result) // Authenticate message expected after Challenge

        val deserializedResult = JSONSerializer().deserialize(result!!)
        assertTrue(deserializedResult is Authenticate)

        assertThrows(SessionNotReady::class.java) { joiner.getSessionDetails() }

        val welcomeMessage = Welcome(testSessionID, clientRoles, testAuthID, testAuthRole, testAuthMethod)
        val serializedWelcome = JSONSerializer().serialize(welcomeMessage)

        val finalResult = joiner.receive(serializedWelcome)
        assertNull(finalResult) // No further message expected after Welcome

        val sessionDetails = joiner.getSessionDetails()
        assertEquals(testSessionID, sessionDetails.sessionID)
        assertEquals(testRealm, sessionDetails.realm)
        assertEquals(testAuthID, sessionDetails.authid)
        assertEquals(testAuthRole, sessionDetails.authrole)
    }

    @Test
    fun receiveAbortMessage() {
        val joiner = Joiner(testRealm)
        joiner.sendHello()

        val abortMessage = Abort(emptyMap(), "some.message")
        val serializedAbort = JSONSerializer().serialize(abortMessage)

        assertThrows(ApplicationError::class.java) { joiner.receive(serializedAbort) }
    }
}
