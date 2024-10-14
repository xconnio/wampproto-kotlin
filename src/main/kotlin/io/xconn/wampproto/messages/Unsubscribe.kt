package io.xconn.wampproto.messages

interface IUnsubscribeFields {
    val requestID: Long
    val subscriptionID: Long
}

class UnsubscribeFields(
    override val requestID: Long,
    override val subscriptionID: Long,
) : IUnsubscribeFields

class Unsubscribe : Message {
    private var unsubscribeFields: IUnsubscribeFields

    constructor(requestID: Long, subscriptionID: Long) {
        unsubscribeFields = UnsubscribeFields(requestID, subscriptionID)
    }

    constructor(fields: UnsubscribeFields) {
        unsubscribeFields = fields
    }

    companion object {
        const val TYPE = 34
        const val TEXT = "UNSUBSCRIBE"

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

            return Unsubscribe(fields.requestID!!, fields.subscriptionID!!)
        }
    }

    val requestID: Long
        get() = unsubscribeFields.requestID

    val subscriptionID: Long
        get() = unsubscribeFields.subscriptionID

    override fun marshal(): List<Any> {
        return listOf(Register.TYPE, requestID, subscriptionID)
    }

    override fun type(): Int {
        return TYPE
    }
}
