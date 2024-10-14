package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Yield
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Yield, msg2: Yield): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.options == msg2.options &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testYieldMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message yield 1 -o foo=bar --serializer $serializerStr --output hex" to
                Yield(1, options = mapOf("foo" to "bar")),
            "wampproto message yield 1 -o foo=bar args --serializer $serializerStr --output hex" to
                Yield(1, args = listOf("args"), options = mapOf("foo" to "bar")),
            "wampproto message yield 1 -o foo=bar -k k=v --serializer $serializerStr --output hex" to
                Yield(1, kwargs = mapOf("k" to "v"), options = mapOf("foo" to "bar")),
            "wampproto message yield 1 -o foo=bar args -k k=v --serializer $serializerStr --output hex" to
                Yield(1, args = listOf("args"), kwargs = mapOf("k" to "v"), options = mapOf("foo" to "bar")),
        )

    for ((command: String, yieldMsg: Yield) in testCases) {
        var message = yieldMsg
        if (message.args == null && message.kwargs != null) {
            message = Yield(message.requestID, emptyList(), message.kwargs, message.options)
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Yield))
    }
}

class YieldMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testYieldMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testYieldMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testYieldMessage("msgpack", serializer)
    }
}
