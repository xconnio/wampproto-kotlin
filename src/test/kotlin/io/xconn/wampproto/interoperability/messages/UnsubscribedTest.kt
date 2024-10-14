package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Unsubscribed
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Unsubscribed, msg2: Unsubscribed): Boolean {
    return msg1.requestID == msg2.requestID
}

fun testUnsubscribedMessage(serializerStr: String, serializer: Serializer) {
    val message = Unsubscribed(1)
    val command = "wampproto message unsubscribed ${message.requestID} --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Unsubscribed))
}

class UnsubscribedMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testUnsubscribedMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testUnsubscribedMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testUnsubscribedMessage("msgpack", serializer)
    }
}
