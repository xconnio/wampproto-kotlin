package io.xconn.wampproto

import io.xconn.wampproto.auth.AnonymousAuthenticator
import io.xconn.wampproto.auth.ClientAuthenticator
import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Challenge
import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.messages.Message
import io.xconn.wampproto.messages.Welcome
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.Serializer

val clientRoles: Map<String, Map<String, Map<String, Any>>> =
    mapOf(
        "caller" to mapOf("features" to emptyMap()),
        "callee" to mapOf("features" to emptyMap()),
        "publisher" to mapOf("features" to emptyMap()),
        "subscriber" to mapOf("features" to emptyMap()),
    )

class Joiner(
    private val realm: String,
    private val serializer: Serializer = JSONSerializer(),
    private val authenticator: ClientAuthenticator = AnonymousAuthenticator(""),
) {
    private val stateNone = 0
    private val stateHelloSent = 1
    private val stateAuthenticateSent = 2
    private val stateJoined = 3

    private var state = stateNone
    private var sessionDetails: SessionDetails? = null

    fun sendHello(): Any {
        val hello = Hello(realm, clientRoles, authenticator.authID, listOf(authenticator.authMethod), authenticator.authExtra)
        state = stateHelloSent

        return serializer.serialize(hello)
    }

    fun receive(data: Any): Any? {
        val receivedMessage = serializer.deserialize(data)
        val toSend = receiveMessage(receivedMessage)
        if (toSend is Authenticate) {
            return serializer.serialize(toSend)
        }

        return null
    }

    private fun receiveMessage(msg: Message): Message? {
        return when (msg) {
            is Welcome -> {
                if (state != stateHelloSent && state != stateAuthenticateSent) {
                    throw ProtocolError("received welcome when it was not expected")
                }

                sessionDetails = SessionDetails(msg.sessionID, realm, msg.authID, msg.authRole)
                state = stateJoined

                null
            }
            is Challenge -> {
                if (state != stateHelloSent) {
                    throw ProtocolError("received challenge when it was not expected")
                }

                val authenticate = authenticator.authenticate(msg)
                state = stateAuthenticateSent

                authenticate
            }
            is Abort -> throw ApplicationError(msg.reason, msg.args, msg.kwargs)
            else -> throw ProtocolError("received ${msg::class.simpleName} message and session is not established yet")
        }
    }

    fun getSessionDetails(): SessionDetails {
        return sessionDetails ?: throw SessionNotReady("session is not set up yet")
    }
}
