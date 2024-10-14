package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Subscribe
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Subscribe, msg2: Subscribe): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.topic == msg2.topic &&
        msg1.options == msg2.options
}

fun testSubscribeMessage(serializerStr: String, serializer: Serializer) {
    val message = Subscribe(1, "io.xconn", mapOf("foo" to "abc"))
    val command =
        "wampproto message subscribe ${message.requestID} ${message.topic} -o foo=abc " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Subscribe))
}

class SubscribeMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testSubscribeMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testSubscribeMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testSubscribeMessage("msgpack", serializer)
    }
}
