package io.xconn.wampproto

import io.xconn.wampproto.messages.Abort
import io.xconn.wampproto.messages.Call
import io.xconn.wampproto.messages.Error
import io.xconn.wampproto.messages.Event
import io.xconn.wampproto.messages.Goodbye
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
import io.xconn.wampproto.messages.Yield
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.Serializer

class Session(private val serializer: Serializer = JSONSerializer()) {
    // data structures for RPC
    private val callRequests = mutableMapOf<Long, Long>()
    private val registerRequests = mutableMapOf<Long, Long>()
    private val registrations = mutableMapOf<Long, Long>()
    private val invocationRequests = mutableMapOf<Long, Long>()
    private val unregisterRequests = mutableMapOf<Long, Long>()

    // data structures for PubSub
    private val publishRequests = mutableMapOf<Long, Long>()
    private val subscribeRequests = mutableMapOf<Long, Long>()
    private val subscriptions = mutableMapOf<Long, Long>()
    private val unsubscribeRequests = mutableMapOf<Long, Long>()

    fun sendMessage(msg: Message): Any {
        return when (msg) {
            is Call -> {
                callRequests[msg.requestID] = msg.requestID

                serializer.serialize(msg)
            }
            is Register -> {
                registerRequests[msg.requestID] = msg.requestID

                serializer.serialize(msg)
            }
            is Unregister -> {
                unregisterRequests[msg.requestID] = msg.registrationID

                serializer.serialize(msg)
            }
            is Yield -> {
                if (!invocationRequests.containsKey(msg.requestID)) {
                    throw IllegalArgumentException("cannot yield for unknown invocation request")
                }

                invocationRequests.remove(msg.requestID)

                serializer.serialize(msg)
            }
            is Publish -> {
                if (msg.options["acknowledge"] == true) {
                    publishRequests[msg.requestID] = msg.requestID
                }

                serializer.serialize(msg)
            }
            is Subscribe -> {
                subscribeRequests[msg.requestID] = msg.requestID

                serializer.serialize(msg)
            }
            is Unsubscribe -> {
                unsubscribeRequests[msg.requestID] = msg.subscriptionID

                serializer.serialize(msg)
            }
            is Error -> {
                if (msg.messageType != Invocation.TYPE) {
                    throw IllegalArgumentException("send only supported for invocation error")
                }

                invocationRequests.remove(msg.requestID)

                serializer.serialize(msg)
            }
            is Goodbye -> {
                serializer.serialize(msg)
            }
            else -> throw ProtocolError("unknown message ${msg::class.simpleName}")
        }
    }

    fun receive(data: Any): Message {
        val msg = serializer.deserialize(data)

        return receiveMessage(msg)
    }

    private fun receiveMessage(msg: Message): Message {
        return when (msg) {
            is Result -> {
                if (!callRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received RESULT for invalid request ID ${msg.requestID}")
                }

                callRequests.remove(msg.requestID)

                msg
            }
            is Registered -> {
                if (!registerRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received REGISTERED for invalid request ID ${msg.requestID}")
                }

                registerRequests.remove(msg.requestID)
                registrations[msg.registrationID] = msg.registrationID

                msg
            }
            is Unregistered -> {
                if (!unregisterRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received UNREGISTERED for invalid request ID ${msg.requestID}")
                }

                val registrationID = unregisterRequests.remove(msg.requestID)!!
                if (!registrations.containsKey(registrationID)) {
                    throw ProtocolError("received UNREGISTERED for invalid registration ID $registrationID")
                }
                registrations.remove(registrationID)

                msg
            }
            is Invocation -> {
                if (!registrations.containsKey(msg.registrationID)) {
                    throw ProtocolError("received INVOCATION for invalid registration ID ${msg.registrationID}")
                }

                invocationRequests[msg.requestID] = msg.requestID

                msg
            }
            is Published -> {
                if (!publishRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received PUBLISHED for invalid request ID ${msg.requestID}")
                }

                publishRequests.remove(msg.requestID)

                msg
            }
            is Subscribed -> {
                if (!subscribeRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received SUBSCRIBED for invalid request ID ${msg.requestID}")
                }

                subscribeRequests.remove(msg.requestID)
                subscriptions[msg.subscriptionID] = msg.subscriptionID

                msg
            }
            is Unsubscribed -> {
                if (!unsubscribeRequests.containsKey(msg.requestID)) {
                    throw ProtocolError("received UNSUBSCRIBED for invalid request ID ${msg.requestID}")
                }

                val subscriptionID = unsubscribeRequests.remove(msg.requestID)
                if (!subscriptions.containsKey(subscriptionID)) {
                    throw ProtocolError("received UNSUBSCRIBED for invalid subscription ID $subscriptionID")
                }
                subscriptions.remove(subscriptionID)

                msg
            }
            is Event -> {
                if (!subscriptions.containsKey(msg.subscriptionID)) {
                    throw ProtocolError("received EVENT for invalid subscription ID ${msg.subscriptionID}")
                }

                msg
            }
            is Error -> {
                when (msg.messageType) {
                    Call.TYPE -> {
                        if (!callRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid call request")
                        }

                        callRequests.remove(msg.requestID)
                    }
                    Register.TYPE -> {
                        if (!registerRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid register request")
                        }

                        registerRequests.remove(msg.requestID)
                    }
                    Unregister.TYPE -> {
                        if (!unregisterRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid unregister request")
                        }

                        unregisterRequests.remove(msg.requestID)
                    }
                    Subscribe.TYPE -> {
                        if (!subscribeRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid subscribe request")
                        }

                        subscribeRequests.remove(msg.requestID)
                    }
                    Unsubscribe.TYPE -> {
                        if (!unsubscribeRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid unsubscribe request")
                        }

                        unsubscribeRequests.remove(msg.requestID)
                    }
                    Publish.TYPE -> {
                        if (!publishRequests.containsKey(msg.requestID)) {
                            throw ProtocolError("received ERROR for invalid publish request")
                        }

                        publishRequests.remove(msg.requestID)
                    }
                    else -> throw ProtocolError("unknown error message type ${msg::class.simpleName}")
                }

                msg
            }
            is Goodbye -> {
                msg
            }
            is Abort -> {
                msg
            }
            else -> throw ProtocolError("unknown message ${msg::class.simpleName}")
        }
    }
}
