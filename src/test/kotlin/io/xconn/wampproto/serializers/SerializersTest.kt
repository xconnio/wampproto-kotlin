package io.xconn.wampproto.serializers

import io.xconn.wampproto.messages.Hello
import kotlin.test.Test
import kotlin.test.assertTrue

class SerializersTest {
    private val hello = Hello("realm1", emptyMap(), "hello", emptyList(), emptyMap())

    @Test
    fun json() {
        val serializer = JSONSerializer()
        val serialized = serializer.serialize(hello)
        val helloAgain = serializer.deserialize(serialized)
        assertTrue { helloAgain is Hello }
    }

    @Test
    fun cbor() {
        val serializer = CBORSerializer()
        val serialized = serializer.serialize(hello)
        val helloAgain = serializer.deserialize(serialized)
        assertTrue { helloAgain is Hello }
    }

    @Test
    fun msgpack() {
        val serializer = MsgPackSerializer()
        val serialized = serializer.serialize(hello)
        val helloAgain = serializer.deserialize(serialized)
        assertTrue { helloAgain is Hello }
    }
}
