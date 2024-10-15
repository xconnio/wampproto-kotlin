package io.xconn.wampproto.messages

interface IErrorFields : BinaryPayload {
    val messageType: Int
    val requestID: Long
    val uri: String
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val details: Map<String, Any>
}

class ErrorFields(
    override val messageType: Int,
    override val requestID: Long,
    override val uri: String,
    override val args: List<Any>?,
    override val kwargs: Map<String, Any>?,
    override val details: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IErrorFields

class Error : Message {
    private var errorFields: IErrorFields

    constructor(
        messageType: Int,
        requestID: Long,
        uri: String,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        details: Map<String, Any> = emptyMap(),
    ) {
        errorFields = ErrorFields(messageType, requestID, uri, args, kwargs, details)
    }

    constructor(fields: IErrorFields) {
        errorFields = fields
    }

    companion object {
        const val TYPE: Int = 8
        const val TEXT: String = "ERROR"

        private val validationSpec =
            ValidationSpec(
                minLength = 5,
                maxLength = 7,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateMessageType,
                        2 to ::validateRequestID,
                        3 to ::validateDetails,
                        4 to ::validateUri,
                        5 to ::validateArgs,
                        6 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Error {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Error(
                fields.messageType!!,
                fields.requestID!!,
                fields.uri!!,
                fields.args,
                fields.kwargs,
                fields.details!!,
            )
        }
    }

    val messageType: Int
        get() = errorFields.messageType

    val requestID: Long
        get() = errorFields.requestID

    val uri: String
        get() = errorFields.uri

    val args: List<Any>?
        get() = errorFields.args

    val kwargs: Map<String, Any>?
        get() = errorFields.kwargs

    val details: Map<String, Any>
        get() = errorFields.details

    val payload: ByteArray?
        get() = errorFields.payload

    val payloadSerializer: Int
        get() = errorFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = errorFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, messageType, requestID, details, uri)

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
