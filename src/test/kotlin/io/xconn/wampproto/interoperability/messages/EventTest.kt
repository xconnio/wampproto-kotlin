package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Event
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Event, msg2: Event): Boolean {
    return msg1.subscriptionID == msg2.subscriptionID &&
        msg1.publicationID == msg2.publicationID &&
        msg1.details == msg2.details &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testEventMessage(serializerStr: String, serializer: Serializer) {
    val testCases =
        mapOf(
            "wampproto message event 1 1 --serializer $serializerStr --output hex" to
                Event(1, 1),
            "wampproto message event 1 1 abc --serializer $serializerStr --output hex" to
                Event(1, 1, args = listOf("abc")),
            "wampproto message event 1 1 abc -d abc=1 -k a=1 --serializer $serializerStr --output hex" to
                Event(1, 1, args = listOf("abc"), kwargs = mapOf("a" to 1), details = mapOf("abc" to 1)),
            "wampproto message event 1 1 -k a=1 --serializer $serializerStr --output hex" to
                Event(1, 1, kwargs = mapOf("a" to 1)),
        )

    for ((command, eventMsg) in testCases) {
        var message = eventMsg
        if (message.args == null && message.kwargs != null) {
            message = Event(message.subscriptionID, message.publicationID, emptyList(), message.kwargs, message.details)
        }

        val msg = runCommandAndDeserialize(serializer, command)
        assertTrue(isEqual(message, msg as Event))
    }
}

class EventMessageTest {
    @Test
    fun testJSONSerializer() {
        val serializer = JSONSerializer()
        testEventMessage("json", serializer)
    }

    @Test
    fun testCBORSerializer() {
        val serializer = CBORSerializer()
        testEventMessage("cbor", serializer)
    }

    @Test
    fun testMsgPackSerializer() {
        val serializer = MsgPackSerializer()
        testEventMessage("msgpack", serializer)
    }
}
