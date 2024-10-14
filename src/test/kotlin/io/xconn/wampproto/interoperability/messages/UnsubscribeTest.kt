package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Unsubscribe
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Unsubscribe, msg2: Unsubscribe): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.subscriptionID == msg2.subscriptionID
}

fun testUnsubscribeMessage(serializerStr: String, serializer: Serializer) {
    val message = Unsubscribe(1, 5)
    val command =
        "wampproto message unsubscribe ${message.requestID} ${message.subscriptionID} " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Unsubscribe))
}

class UnsubscribeMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testUnsubscribeMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testUnsubscribeMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testUnsubscribeMessage("msgpack", serializer)
    }
}
