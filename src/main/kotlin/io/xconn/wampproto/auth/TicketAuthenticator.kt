package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge

class TicketAuthenticator(
    override val authID: String,
    override val authExtra: Map<String, Any> = emptyMap(),
    private val ticket: String,
) : ClientAuthenticator {
    companion object {
        const val TYPE = "ticket"
    }

    override val authMethod: String = TYPE

    override fun authenticate(challenge: Challenge): Authenticate {
        return Authenticate(ticket, emptyMap())
    }
}
