package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Welcome
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Welcome, msg2: Welcome): Boolean {
    return msg1.sessionID == msg2.sessionID &&
        msg1.authID == msg2.authID &&
        msg1.authMethod == msg2.authMethod &&
        msg1.authRole == msg2.authRole &&
        msg1.roles == msg2.roles &&
        msg1.authExtra == msg2.authExtra
}

fun testWelcomeMessage(serializerStr: String, serializer: Serializer) {
    val authID = "foo"
    val anonymous = "anonymous"
    val message =
        Welcome(
            1,
            mapOf("callee" to true),
            authID,
            anonymous,
            anonymous,
            mapOf("foo" to "bar"),
        )

    val command =
        "wampproto message welcome 1 --authmethod=$anonymous --authid=$authID --authrole=$anonymous " +
            "--roles callee=true -e foo=bar --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Welcome))
}

class WelcomeMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testWelcomeMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testWelcomeMessage("cbor", serializer)
    }

    @Test
    fun msgpackSerializer() {
        val serializer = MsgPackSerializer()
        testWelcomeMessage("msgpack", serializer)
    }
}
