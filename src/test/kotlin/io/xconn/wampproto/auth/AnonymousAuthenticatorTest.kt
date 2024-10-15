package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Challenge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AnonymousAuthenticatorTest {
    private val authID = "authID"
    private val authExtra = mapOf("extra" to "data")
    private val authenticator = AnonymousAuthenticator(authID, authExtra)

    @Test
    fun constructor() {
        assertNotNull(authenticator)
        assertEquals(authID, authenticator.authID)
        assertEquals(authExtra, authenticator.authExtra)
        assertEquals("anonymous", authenticator.authMethod)
    }

    @Test
    fun authenticate() {
        val challenge = Challenge(authenticator.authMethod, mapOf("challenge" to "test"))
        assertThrows<UnsupportedOperationException> {
            authenticator.authenticate(challenge)
        }
    }
}
