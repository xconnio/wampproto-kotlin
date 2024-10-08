package io.xconn.wampproto.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import io.xconn.wampproto.messages.Message
import org.msgpack.jackson.dataformat.MessagePackFactory

class MsgPackSerializer : Serializer {
    private val mapper = ObjectMapper(MessagePackFactory())

    override fun serialize(msg: Message): Any {
        return mapper.writeValueAsBytes(msg.marshal())
    }

    override fun deserialize(data: Any): Message {
        val list =
            (mapper.readValue(data as ByteArray, List::class.java) as? List<Any>)
                ?: throw Exception("Failed to cast message to List<Any>")
        return toMessage(list)
    }
}
