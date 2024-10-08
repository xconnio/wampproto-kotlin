import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.serializers.CBORSerializer
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.MsgPackSerializer
import kotlin.test.Test
import kotlin.test.assertTrue

class TestSerializers {
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
