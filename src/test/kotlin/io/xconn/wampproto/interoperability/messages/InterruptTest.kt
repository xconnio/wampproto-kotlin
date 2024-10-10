package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Interrupt
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Interrupt, msg2: Interrupt): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.options == msg2.options
}

fun testInterruptMessage(serializerStr: String, serializer: Serializer) {
    val message = Interrupt(1, mapOf("foo" to "abc"))
    val command = "wampproto message interrupt ${message.requestID} -o foo=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Interrupt))
}

class InterruptMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testInterruptMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testInterruptMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testInterruptMessage("msgpack", serializer)
    }
}
