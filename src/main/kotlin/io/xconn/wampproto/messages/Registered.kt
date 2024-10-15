package io.xconn.wampproto.messages

interface IRegisteredFields {
    val requestID: Long
    val registrationID: Long
}

class RegisteredFields(
    override val requestID: Long,
    override val registrationID: Long,
) : IRegisteredFields

class Registered : Message {
    private var registeredFields: IRegisteredFields

    constructor(
        requestID: Long,
        registrationID: Long,
    ) {
        registeredFields = RegisteredFields(requestID, registrationID)
    }

    constructor(fields: RegisteredFields) {
        registeredFields = fields
    }

    companion object {
        const val TYPE = 65
        const val TEXT = "REGISTERED"

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
            return Registered(fields.requestID!!, fields.registrationID!!)
        }
    }

    val requestID: Long
        get() = registeredFields.requestID

    val registrationID: Long
        get() = registeredFields.registrationID

    override fun marshal(): List<Any> {
        return listOf(Register.TYPE, requestID, registrationID)
    }

    override fun type(): Int {
        return TYPE
    }
}
