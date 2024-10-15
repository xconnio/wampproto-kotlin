package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Publish
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Publish, msg2: Publish): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.uri == msg2.uri &&
        msg1.options == msg2.options &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testPublishMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message publish 1 io.xconn.topic --serializer $serializerStr --output hex" to
                Publish(1, "io.xconn.topic"),
            "wampproto message publish 1 io.xconn.topic abc --serializer $serializerStr --output hex" to
                Publish(1, "io.xconn.topic", args = listOf("abc")),
            "wampproto message publish 1 io.xconn.topic abc -o foo=bar --serializer $serializerStr --output hex" to
                Publish(1, "io.xconn.topic", args = listOf("abc"), options = mapOf("foo" to "bar")),
            "wampproto message publish 1 io.xconn.topic abc -o foo=bar -k a=1 --serializer $serializerStr --output hex" to
                Publish(1, "io.xconn.topic", args = listOf("abc"), kwargs = mapOf("a" to 1), options = mapOf("foo" to "bar")),
            "wampproto message publish 1 io.xconn.topic -k a=1 --serializer $serializerStr --output hex" to
                Publish(1, "io.xconn.topic", kwargs = mapOf("a" to 1)),
        )

    for ((command, publishMsg) in testCases) {
        var message = publishMsg
        if (message.args == null && message.kwargs != null) {
            message = Publish(message.requestID, message.uri, emptyList(), message.kwargs, message.options)
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Publish))
    }
}

class PublishMessageTest {
    @Test
    fun testJSONSerializer() {
        val serializer = JSONSerializer()
        testPublishMessage("json", serializer)
    }

    @Test
    fun testCBORSerializer() {
        val serializer = CBORSerializer()
        testPublishMessage("cbor", serializer)
    }

    @Test
    fun testMsgPackSerializer() {
        val serializer = MsgPackSerializer()
        testPublishMessage("msgpack", serializer)
    }
}
