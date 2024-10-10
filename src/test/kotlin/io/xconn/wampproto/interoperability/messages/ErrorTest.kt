package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Error, msg2: Error): Boolean {
    return msg1.messageType == msg2.messageType &&
        msg1.requestID == msg2.requestID &&
        msg1.uri == msg2.uri &&
        msg1.details == msg2.details &&
        msg1.args == msg2.args &&
        msg1.kwargs == msg2.kwargs
}

fun testErrorMessage(serializerStr: String, serializer: Serializer) {
    val message = Error(1, 1, "wamp.error", details = emptyMap())
    val command =
        "wampproto message error ${message.messageType} ${message.requestID} ${message.uri}" +
            " --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Error))
}

class ErrorMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testErrorMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testErrorMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testErrorMessage("msgpack", serializer)
    }
}
