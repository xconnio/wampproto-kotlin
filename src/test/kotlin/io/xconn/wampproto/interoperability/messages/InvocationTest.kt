package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Invocation
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Invocation, msg2: Invocation): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.registrationID == msg2.registrationID &&
        msg1.details == msg2.details &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testInvocationMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message invocation 1 5 -d foo=bar --serializer $serializerStr --output hex" to
                Invocation(1, 5, details = mapOf("foo" to "bar")),
            "wampproto message invocation 1 5 -d foo=bar args --serializer $serializerStr --output hex" to
                Invocation(1, 5, args = listOf("args"), details = mapOf("foo" to "bar")),
            "wampproto message invocation 1 5 -d foo=bar -k k=v --serializer $serializerStr --output hex" to
                Invocation(1, 5, kwargs = mapOf("k" to "v"), details = mapOf("foo" to "bar")),
            "wampproto message invocation 1 5 -d foo=bar args -k k=v --serializer $serializerStr --output hex" to
                Invocation(
                    1,
                    5,
                    args = listOf("args"),
                    kwargs = mapOf("k" to "v"),
                    details = mapOf("foo" to "bar"),
                ),
        )

    for ((command: String, invocationMsg: Invocation) in testCases) {
        var message = invocationMsg
        if (message.args == null && message.kwargs != null) {
            message =
                Invocation(
                    message.requestID,
                    message.registrationID,
                    emptyList(),
                    message.kwargs,
                    message.details,
                )
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Invocation))
    }
}

class InvocationMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testInvocationMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testInvocationMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testInvocationMessage("msgpack", serializer)
    }
}
