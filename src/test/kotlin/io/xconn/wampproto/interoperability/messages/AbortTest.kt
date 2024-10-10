package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Abort, msg2: Abort): Boolean {
    return msg1.reason == msg2.reason &&
        msg1.details == msg2.details
}

fun testAbortMessage(serializerStr: String, serializer: Serializer) {
    val message = Abort(mapOf("foo" to "abc"), "crash")
    val command = "wampproto message abort ${message.reason} -d foo=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Abort))
}

class AbortMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testAbortMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testAbortMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testAbortMessage("msgpack", serializer)
    }
}
