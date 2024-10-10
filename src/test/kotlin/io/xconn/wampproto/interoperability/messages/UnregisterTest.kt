package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Unregister
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Unregister, msg2: Unregister): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.registrationID == msg2.registrationID
}

fun testUnregisterMessage(serializerStr: String, serializer: Serializer) {
    val message = Unregister(1, 5)
    val command =
        "wampproto message unregister ${message.requestID} ${message.registrationID} " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Unregister))
}

class UnregisterMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testUnregisterMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testUnregisterMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testUnregisterMessage("msgpack", serializer)
    }
}
