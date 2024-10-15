package io.xconn.wampproto.auth

import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge

interface ClientAuthenticator {
    val authMethod: String
    val authID: String
    val authExtra: Map<String, Any>

    fun authenticate(challenge: Challenge): Authenticate
}
