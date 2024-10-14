package io.xconn.wampproto.messages

interface IUnsubscribedFields {
    val requestID: Long
}

class UnsubscribedFields(
    override val requestID: Long,
) : IUnsubscribedFields

class Unsubscribed : Message {
    private var unsubscribedFields: IUnsubscribedFields

    constructor(requestID: Long) {
        unsubscribedFields = UnsubscribedFields(requestID)
    }

    constructor(fields: UnsubscribedFields) {
        unsubscribedFields = fields
    }

    companion object {
        const val TYPE = 35
        const val TEXT = "UNSUBSCRIBED"

        private val validationSpec =
            ValidationSpec(
                minLength = 2,
                maxLength = 2,
                message = TEXT,
                spec = mapOf(1 to ::validateRequestID),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Unsubscribed(fields.requestID!!)
        }
    }

    val requestID: Long
        get() = unsubscribedFields.requestID

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID)
    }

    override fun type(): Int {
        return TYPE
    }
}
