package io.xconn.wampproto.messages

interface ISubscribedFields {
    val requestID: Long
    val subscriptionID: Long
}

class SubscribedFields(
    override val requestID: Long,
    override val subscriptionID: Long,
) : ISubscribedFields

class Subscribed : Message {
    private var subscribedFields: ISubscribedFields

    constructor(requestID: Long, subscriptionID: Long) {
        subscribedFields = SubscribedFields(requestID, subscriptionID)
    }

    constructor(fields: SubscribedFields) {
        subscribedFields = fields
    }

    companion object {
        const val TYPE = 33
        const val TEXT = "SUBSCRIBED"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateSubscriptionID,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Subscribed(fields.requestID!!, fields.subscriptionID!!)
        }
    }

    val requestID: Long
        get() = subscribedFields.requestID

    val subscriptionID: Long
        get() = subscribedFields.subscriptionID

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, subscriptionID)
    }

    override fun type(): Int {
        return TYPE
    }
}
