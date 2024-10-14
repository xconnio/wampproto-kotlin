package io.xconn.wampproto.messages

interface IResultFields : BinaryPayload {
    val requestID: Long
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val details: Map<String, Any>
}

class ResultFields(
    override val requestID: Long,
    override val args: List<Any>?,
    override val kwargs: Map<String, Any>?,
    override val details: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IResultFields

class Result : Message {
    private var resultFields: IResultFields

    constructor(
        requestID: Long,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        details: Map<String, Any> = emptyMap(),
    ) {
        resultFields = ResultFields(requestID, args, kwargs, details)
    }

    constructor(fields: IResultFields) {
        resultFields = fields
    }

    companion object {
        const val TYPE: Int = 50
        const val TEXT: String = "RESULT"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 5,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateDetails,
                        3 to ::validateArgs,
                        4 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Result {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Result(fields.requestID!!, fields.args, fields.kwargs, fields.details!!)
        }
    }

    val requestID: Long
        get() = resultFields.requestID

    val args: List<Any>?
        get() = resultFields.args

    val kwargs: Map<String, Any>?
        get() = resultFields.kwargs

    val details: Map<String, Any>
        get() = resultFields.details

    val payload: ByteArray?
        get() = resultFields.payload

    val payloadSerializer: Int
        get() = resultFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = resultFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, requestID, details)

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
