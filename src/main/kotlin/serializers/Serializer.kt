package io.xconn.serializers

import io.xconn.messages.Hello
import io.xconn.messages.Message

interface Serializer {
    fun serialize(msg: Message): Any

    fun deserialize(data: Any): Message
}

fun toMessage(data: Array<Any>): Message {
    when (val type = data[0] as Int) {
        Hello.TYPE -> {
            return Hello.parse(data)
        }

        else -> {
            throw IllegalArgumentException("Unsupported type $type")
        }
    }
}
