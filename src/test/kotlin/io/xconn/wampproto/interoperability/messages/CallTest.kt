package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Call
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Call, msg2: Call): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.uri == msg2.uri &&
        msg1.options == msg2.options &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testCallMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message call 1 io.xconn.echo -o foo=bar --serializer $serializerStr --output hex" to
                Call(1, "io.xconn.echo", options = mapOf("foo" to "bar")),
            "wampproto message call 1 io.xconn.echo -o foo=bar args --serializer $serializerStr --output hex" to
                Call(
                    1,
                    "io.xconn.echo",
                    args = listOf("args"),
                    options = mapOf("foo" to "bar"),
                ),
            "wampproto message call 1 io.xconn.echo -o foo=bar -k k=v --serializer $serializerStr --output hex" to
                Call(
                    1,
                    "io.xconn.echo",
                    kwargs = mapOf("k" to "v"),
                    options = mapOf("foo" to "bar"),
                ),
            "wampproto message call 1 io.xconn.echo -o foo=bar args -k k=v --serializer $serializerStr --output hex"
                to
                Call(
                    1,
                    "io.xconn.echo",
                    args = listOf("args"),
                    kwargs = mapOf("k" to "v"),
                    options = mapOf("foo" to "bar"),
                ),
        )

    for ((command: String, callMsg: Call) in testCases) {
        var message = callMsg
        if (message.args == null && message.kwargs != null) {
            message =
                Call(
                    message.requestID,
                    message.uri,
                    emptyList(),
                    message.kwargs,
                    message.options,
                )
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Call))
    }
}

class CallMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testCallMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testCallMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testCallMessage("msgpack", serializer)
    }
}
