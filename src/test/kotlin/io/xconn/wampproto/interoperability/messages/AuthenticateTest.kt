package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Authenticate, msg2: Authenticate): Boolean {
    return msg1.signature == msg2.signature &&
        msg1.extra == msg2.extra
}

fun testAuthenticateMessage(serializerStr: String, serializer: Serializer) {
    val message = Authenticate("signature", mapOf("ticket" to "abc"))
    val command = "wampproto message authenticate ${message.signature} -e ticket=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Authenticate))
}

class AuthenticateMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testAuthenticateMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testAuthenticateMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testAuthenticateMessage("msgpack", serializer)
    }
}
