package io.xconn.wampproto.messages

interface IUnregisteredFields {
    val requestID: Long
}

class UnregisteredFields(
    override val requestID: Long,
) : IUnregisteredFields

class Unregistered : Message {
    private var unregisteredFields: IUnregisteredFields

    constructor(requestID: Long) {
        unregisteredFields = UnregisteredFields(requestID)
    }

    constructor(fields: UnregisteredFields) {
        unregisteredFields = fields
    }

    companion object {
        const val TYPE = 67
        const val TEXT = "UNREGISTERED"

        private val validationSpec =
            ValidationSpec(
                minLength = 2,
                maxLength = 2,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Unregistered(fields.requestID!!)
        }
    }

    val requestID: Long
        get() = unregisteredFields.requestID

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID)
    }

    override fun type(): Int {
        return TYPE
    }
}
