package io.xconn.wampproto.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.xconn.wampproto.messages.Message

class CBORSerializer : Serializer {
    private val mapper = ObjectMapper(CBORFactory())

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
