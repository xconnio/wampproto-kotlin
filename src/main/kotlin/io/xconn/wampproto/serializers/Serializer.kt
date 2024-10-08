package io.xconn.wampproto.serializers

import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.messages.Message

interface Serializer {
    fun serialize(msg: Message): Any

    fun deserialize(data: Any): Message
}

fun toMessage(data: List<Any>): Message {
    when (val type = data[0] as Int) {
        Hello.TYPE -> {
            return Hello.parse(data)
        }

        else -> {
            throw IllegalArgumentException("Unsupported type $type")
        }
    }
}
