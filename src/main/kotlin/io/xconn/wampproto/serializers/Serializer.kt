package io.xconn.wampproto.serializers

import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.messages.Authenticate
import io.xconn.wampproto.messages.Call
import io.xconn.wampproto.messages.Cancel
import io.xconn.wampproto.messages.Challenge
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.messages.Event
import io.xconn.wampproto.messages.Goodbye
import io.xconn.wampproto.messages.Hello
import io.xconn.wampproto.messages.Interrupt
import io.xconn.wampproto.messages.Invocation
import io.xconn.wampproto.messages.Message
import io.xconn.wampproto.messages.Publish
import io.xconn.wampproto.messages.Published
import io.xconn.wampproto.messages.Register
import io.xconn.wampproto.messages.Registered
import io.xconn.wampproto.messages.Result
import io.xconn.wampproto.messages.Subscribe
import io.xconn.wampproto.messages.Subscribed
import io.xconn.wampproto.messages.Unregister
import io.xconn.wampproto.messages.Unregistered
import io.xconn.wampproto.messages.Unsubscribe
import io.xconn.wampproto.messages.Unsubscribed
import io.xconn.wampproto.messages.Welcome
import io.xconn.wampproto.messages.Yield

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
        Subscribe.TYPE -> {
            return Subscribe.parse(data)
        }
        Subscribed.TYPE -> {
            return Subscribed.parse(data)
        }
        Unsubscribe.TYPE -> {
            return Unsubscribe.parse(data)
        }
        Unsubscribed.TYPE -> {
            return Unsubscribed.parse(data)
        }
        Call.TYPE -> {
            return Call.parse(data)
        }
        Invocation.TYPE -> {
            return Invocation.parse(data)
        }
        Yield.TYPE -> {
            return Yield.parse(data)
        }
        Result.TYPE -> {
            return Result.parse(data)
        }
        Publish.TYPE -> {
            return Publish.parse(data)
        }
        Published.TYPE -> {
            return Published.parse(data)
        }
        Event.TYPE -> {
            return Event.parse(data)
        }

        else -> {
            throw IllegalArgumentException("Unsupported type $type")
        }
    }
}
