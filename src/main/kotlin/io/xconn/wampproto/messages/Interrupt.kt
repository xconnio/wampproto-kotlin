package io.xconn.wampproto.messages

interface IInterruptFields {
    val requestID: Long
    val options: Map<String, Any>
}

class InterruptFields(
    override val requestID: Long,
    override val options: Map<String, Any> = emptyMap(),
) : IInterruptFields

class Interrupt : Message {
    private var interruptFields: IInterruptFields

    constructor(requestID: Long, options: Map<String, Any> = emptyMap()) {
        interruptFields = InterruptFields(requestID, options)
    }

    constructor(fields: InterruptFields) {
        interruptFields = fields
    }

    companion object {
        const val TYPE = 69
        const val TEXT = "INTERRUPT"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateOptions,
                    ),
            )

        fun parse(message: List<Any>): Interrupt {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Interrupt(fields.requestID!!, fields.options!!)
        }
    }

    val requestID: Long
        get() = interruptFields.requestID

    val options: Map<String, Any>
        get() = interruptFields.options

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, options)
    }

    override fun type(): Int {
        return TYPE
    }
}
