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
        val raw =
            mapper.readValue(data as ByteArray, List::class.java)
                as? List<Any> ?: throw Exception("Failed to cast message to List<Any>")

        val list =
            raw.map {
                when (it) {
                    is Long -> if (it in Int.MIN_VALUE..Int.MAX_VALUE) it.toInt() else it
                    else -> it
                }
            }

        return toMessage(list)
    }
}
