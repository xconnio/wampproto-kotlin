package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Goodbye
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Goodbye, msg2: Goodbye): Boolean {
    return msg1.reason == msg2.reason &&
        msg1.details == msg2.details
}

fun testGoodbyeMessage(serializerStr: String, serializer: Serializer) {
    val message = Goodbye(mapOf("foo" to "abc"), "crash")
    val command = "wampproto message goodbye ${message.reason} -d foo=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Goodbye))
}

class GoodbyeMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testGoodbyeMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testGoodbyeMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testGoodbyeMessage("msgpack", serializer)
    }
}
