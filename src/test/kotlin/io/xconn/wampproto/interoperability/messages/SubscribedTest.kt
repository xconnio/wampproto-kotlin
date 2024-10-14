package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Subscribed
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Subscribed, msg2: Subscribed): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.subscriptionID == msg2.subscriptionID
}

fun testSubscribedMessage(serializerStr: String, serializer: Serializer) {
    val message = Subscribed(1, 5)
    val command =
        "wampproto message subscribed ${message.requestID} ${message.subscriptionID} " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Subscribed))
}

class SubscribedMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testSubscribedMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testSubscribedMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testSubscribedMessage("msgpack", serializer)
    }
}
