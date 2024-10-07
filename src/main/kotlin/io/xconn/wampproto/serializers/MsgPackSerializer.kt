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
        val o = mapper.readValue(data as ByteArray, Array<Any>::class.java)
        return toMessage(o)
    }
}
