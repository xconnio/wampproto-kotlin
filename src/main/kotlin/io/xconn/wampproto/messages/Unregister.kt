package io.xconn.wampproto.messages

interface IUnregisterFields {
    val requestID: Long
    val registrationID: Long
}

class UnregisterFields(
    override val requestID: Long,
    override val registrationID: Long,
) : IUnregisterFields

class Unregister : Message {
    private var unregisterFields: IUnregisterFields

    constructor(
        requestID: Long,
        registrationID: Long,
    ) {
        unregisterFields = UnregisterFields(requestID, registrationID)
    }

    constructor(fields: UnregisterFields) {
        unregisterFields = fields
    }

    companion object {
        const val TYPE = 66
        const val TEXT = "UNREGISTER"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateRegistrationID,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Unregister(fields.requestID!!, fields.registrationID!!)
        }
    }

    val requestID: Long
        get() = unregisterFields.requestID

    val registrationID: Long
        get() = unregisterFields.registrationID

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, registrationID)
    }

    override fun type(): Int {
        return TYPE
    }
}
