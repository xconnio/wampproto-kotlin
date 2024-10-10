package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Register
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Register, msg2: Register): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.uri == msg2.uri &&
        msg1.options == msg2.options
}

fun testRegisterMessage(serializerStr: String, serializer: Serializer) {
    val message = Register(1, "io.xconn", mapOf("foo" to "abc"))
    val command =
        "wampproto message register ${message.requestID} ${message.uri} -o foo=abc " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Register))
}

class RegisterMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testRegisterMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testRegisterMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testRegisterMessage("msgpack", serializer)
    }
}
