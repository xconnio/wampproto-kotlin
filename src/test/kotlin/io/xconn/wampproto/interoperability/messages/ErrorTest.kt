package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Error, msg2: Error): Boolean {
    return msg1.messageType == msg2.messageType &&
        msg1.requestID == msg2.requestID &&
        msg1.uri == msg2.uri &&
        msg1.details == msg2.details &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testErrorMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message error 1 3 io.xconn.echo -d foo=bar --serializer $serializerStr --output hex" to
                Error(1, 3, "io.xconn.echo", details = mapOf("foo" to "bar")),
            "wampproto message error 1 3 io.xconn.echo -d foo=bar args --serializer $serializerStr --output hex" to
                Error(
                    1,
                    3,
                    "io.xconn.echo",
                    args = listOf("args"),
                    details = mapOf("foo" to "bar"),
                ),
            "wampproto message error 1 3 io.xconn.echo -d foo=bar -k k=v --serializer $serializerStr --output hex" to
                Error(
                    1,
                    3,
                    "io.xconn.echo",
                    kwargs = mapOf("k" to "v"),
                    details = mapOf("foo" to "bar"),
                ),
            "wampproto message error 1 3 io.xconn.echo -d foo=bar args -k k=v --serializer $serializerStr --output hex"
                to
                Error(
                    1,
                    3,
                    "io.xconn.echo",
                    args = listOf("args"),
                    kwargs = mapOf("k" to "v"),
                    details = mapOf("foo" to "bar"),
                ),
        )

    for ((command: String, errorMsg: Error) in testCases) {
        var message = errorMsg
        if (message.args == null && message.kwargs != null) {
            message =
                Error(
                    message.messageType,
                    message.requestID,
                    message.uri,
                    emptyList(),
                    message.kwargs,
                    message.details,
                )
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Error))
    }
}

class ErrorMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testErrorMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testErrorMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testErrorMessage("msgpack", serializer)
    }
}
