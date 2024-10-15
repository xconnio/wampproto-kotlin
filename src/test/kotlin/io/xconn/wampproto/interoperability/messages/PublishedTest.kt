package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Published
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Published, msg2: Published): Boolean {
    return msg1.requestID == msg2.requestID &&
        msg1.publicationID == msg2.publicationID
}

fun testPublishedMessage(serializerStr: String, serializer: Serializer) {
    val message = Published(1, 1)
    val command =
        "wampproto message published ${message.requestID} ${message.publicationID} " +
            "--serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Published))
}

class PublishedMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testPublishedMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testPublishedMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testPublishedMessage("msgpack", serializer)
    }
}
