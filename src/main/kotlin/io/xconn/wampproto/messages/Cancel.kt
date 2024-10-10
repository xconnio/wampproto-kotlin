package io.xconn.wampproto.messages

interface ICancelFields {
    val requestID: Long
    val options: Map<String, Any>
}

class CancelFields(
    override val requestID: Long,
    override val options: Map<String, Any> = emptyMap(),
) : ICancelFields

class Cancel : Message {
    private var cancelFields: ICancelFields

    constructor(requestID: Long, options: Map<String, Any> = emptyMap()) {
        cancelFields = CancelFields(requestID, options)
    }

    constructor(fields: CancelFields) {
        cancelFields = fields
    }

    companion object {
        const val TYPE = 49
        const val TEXT = "CANCEL"

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

        fun parse(message: List<Any>): Cancel {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Cancel(fields.requestID!!, fields.options!!)
        }
    }

    val requestID: Long
        get() = cancelFields.requestID

    val options: Map<String, Any>
        get() = cancelFields.options

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, options)
    }

    override fun type(): Int {
        return TYPE
    }
}
