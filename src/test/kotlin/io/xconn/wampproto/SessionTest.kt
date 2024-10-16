package io.xconn.wampproto

import io.xconn.wampproto.interoperability.messages.isEqual
import io.xconn.wampproto.messages.Call
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.messages.Event
import io.xconn.wampproto.messages.Invocation
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
import io.xconn.wampproto.messages.Yield
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class SessionTest {
    private var serializer: Serializer = JSONSerializer()
    private var session: Session = Session(serializer)

    @Test
    fun sendRegister() {
        val register = Register(2, "io.xconn.test")
        val toSend = session.sendMessage(register)
        assertEquals("""[${Register.TYPE},${register.requestID},${register.options},"${register.uri}"]""", toSend)

        val registered = Registered(2, 3)
        val received = session.receive(serializer.serialize(registered))
        isEqual(registered, received as Registered)
    }

    @Test
    fun sendCall() {
        val call = Call(10, "io.xconn.test")
        val toSend = session.sendMessage(call)
        assertEquals("""[${Call.TYPE},${call.requestID},${call.options},"${call.uri}"]""", toSend)

        val result = Result(10)
        val received = session.receive(serializer.serialize(result))
        isEqual(result, received as Result)
    }

    @Test
    fun receiveInvocation() {
        // register a procedure
        session.sendMessage(Register(2, "io.xconn.test"))
        session.receive(serializer.serialize(Registered(2, 3)))

        val invocation = Invocation(4, 3)
        val toSend = session.receive(serializer.serialize(invocation))
        isEqual(invocation, toSend as Invocation)

        val yield = Yield(4)
        val received = session.sendMessage(yield)
        assertEquals("[70,4,{}]", received)
    }

    @Test
    fun sendUnregister() {
        // register a procedure
        session.sendMessage(Register(2, "io.xconn.test"))
        session.receive(serializer.serialize(Registered(2, 3)))

        val unregister = Unregister(3, 3)
        val toSend = session.sendMessage(unregister)
        assertEquals("[${Unregister.TYPE},${unregister.requestID},${unregister.registrationID}]", toSend)

        val unregistered = Unregistered(3)
        val received = session.receive(serializer.serialize(unregistered))
        isEqual(unregistered, received as Unregistered)
    }

    @Test
    fun sendPublishWithAcknowledge() {
        val publish = Publish(6, "topic", options = mapOf("acknowledge" to true))
        val toSend = session.sendMessage(publish)
        assertEquals("""[${Publish.TYPE},${publish.requestID},{"acknowledge":true},"${publish.uri}"]""", toSend)

        val published = Published(6, 6)
        val received = session.receive(serializer.serialize(published))
        isEqual(published, received as Published)
    }

    @Test
    fun sendSubscribe() {
        val subscribe = Subscribe(7, "topic")
        val toSend = session.sendMessage(subscribe)
        assertEquals("""[${Subscribe.TYPE},${subscribe.requestID},${subscribe.options},"${subscribe.topic}"]""", toSend)

        val subscribed = Subscribed(7, 8)
        val received = session.receive(serializer.serialize(subscribed))
        isEqual(subscribed, received as Subscribed)

        val event = Event(8, 6)
        val receivedEvent = session.receive(serializer.serialize(event))
        isEqual(event, receivedEvent as Event)
    }

    @Test
    fun sendUnsubscribe() {
        // subscribe a topic
        session.sendMessage(Subscribe(7, "topic"))
        session.receive(serializer.serialize(Subscribed(7, 8)))

        val unsubscribe = Unsubscribe(8, 8)
        val toSend = session.sendMessage(unsubscribe)
        assertEquals("[${Unsubscribe.TYPE},${unsubscribe.requestID},${unsubscribe.subscriptionID}]", toSend)

        val unsubscribed = Unsubscribed(8)
        val received = session.receive(serializer.serialize(unsubscribed))
        isEqual(unsubscribed, received as Unsubscribed)
    }

    @Test
    fun sendError() {
        val error = Error(Invocation.TYPE, 10, errorProcedureAlreadyExists)
        val toSend = session.sendMessage(error)
        assertEquals("""[${Error.TYPE},${Invocation.TYPE},${error.requestID},${error.details},"${error.uri}"]""", toSend)
    }

    @Test
    fun receiveError() {
        // send Call message and receive Error for that Call
        val call = Call(1, "io.xconn.test")
        session.sendMessage(call)

        val callErr = Error(Call.TYPE, call.requestID, errorInvalidArgument)
        val received = session.receive(serializer.serialize(callErr))
        isEqual(callErr, received as Error)

        // send Register message and receive Error for that Register
        val register = Register(2, "io.xconn.test")
        session.sendMessage(register)

        val registerErr = Error(Register.TYPE, register.requestID, errorInvalidArgument)
        val receivedRegisterError = session.receive(serializer.serialize(registerErr))
        isEqual(registerErr, receivedRegisterError as Error)

        // send Unregister message and receive Error for that Unregister
        val unregister = Unregister(3, 3)
        session.sendMessage(unregister)

        val unregisterErr = Error(Unregister.TYPE, unregister.requestID, errorInvalidArgument)
        val receivedUnregisterError = session.receive(serializer.serialize(unregisterErr))
        isEqual(unregisterErr, receivedUnregisterError as Error)

        // send Subscribe message and receive Error for that Subscribe
        val subscribe = Subscribe(7, "topic")
        session.sendMessage(subscribe)

        val subscribeError = Error(Subscribe.TYPE, subscribe.requestID, errorInvalidUri)
        val receivedSubscribeError = session.receive(serializer.serialize(subscribeError))
        isEqual(subscribeError, receivedSubscribeError as Error)

        // send Unsubscribe message and receive Error for that Unsubscribe
        val unsubscribe = Unsubscribe(8, 8)
        session.sendMessage(unsubscribe)

        val unsubscribeError = Error(Unsubscribe.TYPE, unsubscribe.requestID, errorInvalidUri)
        val receivedUnsubscribeError = session.receive(serializer.serialize(unsubscribeError))
        isEqual(unsubscribeError, receivedUnsubscribeError as Error)

        // send Publish message and receive Error for that Publish
        val publish = Publish(6, "topic", options = mapOf("acknowledge" to true))
        session.sendMessage(publish)

        val publishErr = Error(Publish.TYPE, publish.requestID, errorInvalidUri)
        val receivedPublishError = session.receive(serializer.serialize(publishErr))
        isEqual(publishErr, receivedPublishError as Error)
    }

    @Test
    fun exceptions() {
        // send Yield for unknown invocation
        val invalidYield = Yield(5)
        assertThrows<IllegalArgumentException> { session.sendMessage(invalidYield) }

        // send error for invalid message
        val invalidError = Error(Register.TYPE, 10, errorProcedureAlreadyExists)
        assertThrows<IllegalArgumentException> { session.sendMessage(invalidError) }

        // send invalid message
        val invalidMessage = Registered(11, 12)
        assertThrows<Exception> { session.sendMessage(invalidMessage) }

        // receive invalid message
        assertThrows<Exception> { session.receive(serializer.serialize(Register(100, "io.xconn.test"))) }

        // receive error for invalid message
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Registered.TYPE, 100, errorInvalidArgument)))
        }

        // receive error invalid Call id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Call.TYPE, 100, errorInvalidArgument)))
        }

        // receive error Register id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Register.TYPE, 100, errorInvalidArgument)))
        }

        // receive error invalid Unregister id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Unregister.TYPE, 100, errorInvalidArgument)))
        }

        // receive error invalid Subscribe id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Subscribe.TYPE, 100, errorInvalidArgument)))
        }

        // receive error invalid Unsubscribe id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Unsubscribe.TYPE, 100, errorInvalidArgument)))
        }

        // receive error invalid Publish id
        assertThrows<Exception> {
            session.receive(serializer.serialize(Error(Publish.TYPE, 100, errorInvalidArgument)))
        }
    }
}
