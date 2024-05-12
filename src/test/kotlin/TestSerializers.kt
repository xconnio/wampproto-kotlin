import kotlin.test.Test
import kotlin.test.assertTrue

import io.xconn.messages.Hello
import io.xconn.serializers.CBORSerializer
import io.xconn.serializers.JSONSerializer
import io.xconn.serializers.MsgPackSerializer


class TestSerializers {
    private val hello = Hello("realm1", "hello", emptyArray(), emptyMap(), emptyMap())

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
