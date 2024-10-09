package io.xconn.wampproto.interoperability.messages

import io.xconn.wampproto.interoperability.runCommandAndDeserialize
import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

fun isEqual(msg1: Hello, msg2: Hello): Boolean {
    return msg1.authID == msg2.authID &&
        msg1.realm == msg2.realm &&
        msg1.authMethods == msg2.authMethods &&
        msg1.authExtra == msg2.authExtra &&
        msg1.roles == msg2.roles
}

fun testHelloMessage(serializerStr: String, serializer: Serializer) {
    val realm1 = "realm1"
    val authMethod = "anonymous"
    val authID = "foo"
    val message =
        Hello(
            realm1,
            mapOf("callee" to true),
            authID,
            listOf(authMethod),
            mapOf("foo" to "bar"),
        )
    val command =
        "wampproto message hello $realm1 $authMethod --authid $authID -r callee=true -e foo:bar --serializer $serializerStr --output hex"

    val msg = runCommandAndDeserialize(serializer, command)
    assertTrue(isEqual(message, msg as Hello))
}

class HelloMessageTest {
    @Test
    fun jsonSerializer() {
        val serializer = JSONSerializer()
        testHelloMessage("json", serializer)
    }

    @Test
    fun cborSerializer() {
        val serializer = CBORSerializer()
        testHelloMessage("cbor", serializer)
    }

    @Test
    fun msgpackSerializer() {
        val serializer = MsgPackSerializer()
        testHelloMessage("msgpack", serializer)
    }
}
