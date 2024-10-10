package io.xconn.wampproto.serializers

import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Cancel
import io.xconn.wampproto.messages.Challenge
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.messages.Goodbye
import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.messages.Interrupt
import io.xconn.wampproto.messages.Message
import io.xconn.wampproto.messages.Register
import io.xconn.wampproto.messages.Registered
import io.xconn.wampproto.messages.Unregister
import io.xconn.wampproto.messages.Unregistered
import io.xconn.wampproto.messages.Welcome

interface Serializer {
    fun serialize(msg: Message): Any

    fun deserialize(data: Any): Message
}

fun toMessage(data: List<Any>): Message {
    when (val type = data[0] as Int) {
        Hello.TYPE -> {
            return Hello.parse(data)
        }
        Welcome.TYPE -> {
            return Welcome.parse(data)
        }
        Challenge.TYPE -> {
            return Challenge.parse(data)
        }
        Authenticate.TYPE -> {
            return Authenticate.parse(data)
        }
        Abort.TYPE -> {
            return Abort.parse(data)
        }
        Error.TYPE -> {
            return Error.parse(data)
        }
        Cancel.TYPE -> {
            return Cancel.parse(data)
        }
        Interrupt.TYPE -> {
            return Interrupt.parse(data)
        }
        Goodbye.TYPE -> {
            return Goodbye.parse(data)
        }
        Register.TYPE -> {
            return Register.parse(data)
        }
        Registered.TYPE -> {
            return Registered.parse(data)
        }
        Unregister.TYPE -> {
            return Unregister.parse(data)
        }
        Unregistered.TYPE -> {
            return Unregistered.parse(data)
        }

        else -> {
            throw IllegalArgumentException("Unsupported type $type")
        }
    }
}
