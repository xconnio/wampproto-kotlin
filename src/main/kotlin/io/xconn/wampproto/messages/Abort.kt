package io.xconn.wampproto.messages

interface IAbortFields : BinaryPayload {
    val details: Map<String, Any>
    val reason: String
    val args: List<Any>?
    val kwargs: Map<String, Any>?
}

class AbortFields(
    override val details: Map<String, Any>,
    override val reason: String,
    override val args: List<Any>? = null,
    override val kwargs: Map<String, Any>? = null,
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IAbortFields

class Abort : Message {
    private var abortFields: IAbortFields

    constructor(details: Map<String, Any>, reason: String, args: List<Any>? = null, kwargs: Map<String, Any>? = null) {
        abortFields = AbortFields(details, reason, args, kwargs)
    }

    constructor(fields: IAbortFields) {
        abortFields = fields
    }

    companion object {
        const val TYPE: Int = 3
        const val TEXT: String = "ABORT"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 5,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateDetails,
                        2 to ::validateReason,
                        3 to ::validateArgs,
                        4 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Abort {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Abort(fields.details!!, fields.reason!!, fields.args, fields.kwargs)
        }
    }

    val details: Map<String, Any>
        get() = abortFields.details

    val reason: String
        get() = abortFields.reason

    val args: List<Any>?
        get() = abortFields.args

    val kwargs: Map<String, Any>?
        get() = abortFields.kwargs

    val payload: ByteArray?
        get() = abortFields.payload

    val payloadSerializer: Int
        get() = abortFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = abortFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, details, reason)

        args?.let { message.add(it) }

        kwargs?.let {
            if (args == null) {
                message.add(emptyList<Any>())
            }
            message.add(it)
        }

        return message
    }

    override fun type(): Int = TYPE
}
