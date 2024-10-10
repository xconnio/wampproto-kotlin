package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Cancel
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Cancel, msg2: Cancel): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.options == msg2.options
}

fun testCancelMessage(serializerStr: String, serializer: Serializer) {
    val message = Cancel(1, mapOf("foo" to "abc"))
    val command = "wampproto message cancel ${message.requestID} -o foo=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Cancel))
}

class CancelMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testCancelMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testCancelMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testCancelMessage("msgpack", serializer)
    }
}
