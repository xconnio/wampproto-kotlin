package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Result
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Result, msg2: Result): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.details == msg2.details &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testResultMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message result 1 -d foo=bar --serializer $serializerStr --output hex" to
                Result(1, details = mapOf("foo" to "bar")),
            "wampproto message result 1 -d foo=bar args --serializer $serializerStr --output hex" to
                Result(1, args = listOf("args"), details = mapOf("foo" to "bar")),
            "wampproto message result 1 -d foo=bar -k k=v --serializer $serializerStr --output hex" to
                Result(1, kwargs = mapOf("k" to "v"), details = mapOf("foo" to "bar")),
            "wampproto message result 1 -d foo=bar args -k k=v --serializer $serializerStr --output hex" to
                Result(1, args = listOf("args"), kwargs = mapOf("k" to "v"), details = mapOf("foo" to "bar")),
        )

    for ((command: String, resultMsg: Result) in testCases) {
        var message = resultMsg
        if (message.args == null && message.kwargs != null) {
            message = Result(message.requestID, emptyList(), message.kwargs, message.details)
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Result))
    }
}

class ResultMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testResultMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testResultMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testResultMessage("msgpack", serializer)
    }
}
