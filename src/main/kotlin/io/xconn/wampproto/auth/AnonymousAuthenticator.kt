package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge

class AnonymousAuthenticator(
    override val authID: String,
    override val authExtra: Map<String, Any> = emptyMap(),
) : ClientAuthenticator {
    companion object {
        const val TYPE = "anonymous"
    }

    override val authMethod: String = TYPE

    override fun authenticate(challenge: Challenge): Authenticate {
        throw UnsupportedOperationException("func Authenticate() must not be called for anonymous authentication")
    }
}
