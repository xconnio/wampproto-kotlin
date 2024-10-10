package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Unregistered
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Unregistered, msg2: Unregistered): Boolean {
    return msg1.requestID == msg2.requestID
}

fun testUnregisteredMessage(serializerStr: String, serializer: Serializer) {
    val message = Unregistered(1)
    val command =
        "wampproto message unregistered ${message.requestID} --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Unregistered))
}

class UnregisteredMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testUnregisteredMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testUnregisteredMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testUnregisteredMessage("msgpack", serializer)
    }
}
