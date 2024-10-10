package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Registered
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Registered, msg2: Registered): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.registrationID == msg2.registrationID
}

fun testRegisteredMessage(serializerStr: String, serializer: Serializer) {
    val message = Registered(1, 5)
    val command =
        "wampproto message registered ${message.requestID} ${message.registrationID} " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Registered))
}

class RegisteredMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testRegisteredMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testRegisteredMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testRegisteredMessage("msgpack", serializer)
    }
}
