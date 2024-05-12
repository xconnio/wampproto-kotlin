package io.xconn.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.xconn.messages.Message

class JSONSerializer : Serializer {
    private val mapper = ObjectMapper(JsonFactory())

    override fun serialize(msg: Message): Any {
        return mapper.writeValueAsString(msg.marshal())
    }

    override fun deserialize(data: Any): Message {
        val o = mapper.readValue(data as String, Array<Any>::class.java)
        return toMessage(o)
    }
}
