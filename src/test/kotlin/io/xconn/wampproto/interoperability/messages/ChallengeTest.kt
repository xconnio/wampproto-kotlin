package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Challenge
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Challenge, msg2: Challenge): Boolean {
    return msg1.authMethod == msg2.authMethod &&
        msg1.extra == msg2.extra
}

fun testChallengeMessage(serializerStr: String, serializer: Serializer) {
    val message = Challenge("ticket", mapOf("ticket" to "abc"))
    val command = "wampproto message challenge ${message.authMethod} -e ticket=abc --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Challenge))
}

class ChallengeMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testChallengeMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testChallengeMessage("cbor", serializer)
    }

    @Test
    fun msgPackSerializer() {
        val serializer = MsgPackSerializer()
        testChallengeMessage("msgpack", serializer)
    }
}
