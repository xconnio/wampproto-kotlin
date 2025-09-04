package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Challenge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TicketAuthenticatorTest {
    private val authID = "authID"
    private val authExtra = mapOf("extra" to "data")
    private val ticket = "new ticket"
    private val authenticator = TicketAuthenticator(authID, ticket, authExtra)

    @Test
    fun constructor() {
        assertNotNull(authenticator)
        assertEquals(authID, authenticator.authID)
        assertEquals(authExtra, authenticator.authExtra)
        assertEquals("ticket", authenticator.authMethod)
    }

    @Test
    fun authenticate() {
        val challenge = Challenge(authenticator.authMethod, mapOf("challenge" to "test"))
        val authenticate = authenticator.authenticate(challenge)
        assertEquals(authenticate.signature, ticket)
    }
}
