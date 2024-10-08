package io.xconn.wampproto.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.xconn.wampproto.messages.Message

class JSONSerializer : Serializer {
    private val mapper = ObjectMapper(JsonFactory())

    override fun serialize(msg: Message): Any {
        return mapper.writeValueAsString(msg.marshal())
    }

    override fun deserialize(data: Any): Message {
        val list =
            (mapper.readValue(data as String, List::class.java) as? List<Any>)
                ?: throw Exception("Failed to cast message to List<Any>")
        return toMessage(list)
    }
}
