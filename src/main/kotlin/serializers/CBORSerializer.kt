package io.xconn.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.xconn.messages.Message

class CBORSerializer : Serializer {
    private val mapper = ObjectMapper(CBORFactory())

    override fun serialize(msg: Message): Any {
        return mapper.writeValueAsBytes(msg.marshal())
    }

    override fun deserialize(data: Any): Message {
        val o = mapper.readValue(data as ByteArray, Array<Any>::class.java)
        return toMessage(o)
    }
}
