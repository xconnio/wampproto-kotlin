package io.xconn.wampproto.messages

interface ICallFields : BinaryPayload {
    val requestID: Long
    val uri: String
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val options: Map<String, Any>
}

class CallFields(
    override val requestID: Long,
    override val uri: String,
    override val args: List<Any>?,
    override val kwargs: Map<String, Any>?,
    override val options: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : ICallFields

class Call : Message {
    private var callFields: ICallFields

    constructor(
        requestID: Long,
        uri: String,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        options: Map<String, Any> = emptyMap(),
    ) {
        callFields = CallFields(requestID, uri, args, kwargs, options)
    }

    constructor(fields: ICallFields) {
        callFields = fields
    }

    companion object {
        const val TYPE: Int = 48
        const val TEXT: String = "CALL"

        private val validationSpec =
            ValidationSpec(
                minLength = 4,
                maxLength = 6,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateOptions,
                        3 to ::validateUri,
                        4 to ::validateArgs,
                        5 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Call {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Call(fields.requestID!!, fields.uri!!, fields.args, fields.kwargs, fields.options!!)
        }
    }

    val requestID: Long
        get() = callFields.requestID

    val uri: String
        get() = callFields.uri

    val args: List<Any>?
        get() = callFields.args

    val kwargs: Map<String, Any>?
        get() = callFields.kwargs

    val options: Map<String, Any>
        get() = callFields.options

    val payload: ByteArray?
        get() = callFields.payload

    val payloadSerializer: Int
        get() = callFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = callFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, requestID, options, uri)

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
