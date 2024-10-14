package io.xconn.wampproto.messages

interface IYieldFields : BinaryPayload {
    val requestID: Long
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val options: Map<String, Any>
}

class YieldFields(
    override val requestID: Long,
    override val args: List<Any>?,
    override val kwargs: Map<String, Any>?,
    override val options: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IYieldFields

class Yield : Message {
    private var yieldFields: IYieldFields

    constructor(
        requestID: Long,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        options: Map<String, Any> = emptyMap(),
    ) {
        yieldFields = YieldFields(requestID, args, kwargs, options)
    }

    constructor(fields: IYieldFields) {
        yieldFields = fields
    }

    companion object {
        const val TYPE: Int = 70
        const val TEXT: String = "YIELD"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 5,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateOptions,
                        3 to ::validateArgs,
                        4 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Yield {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Yield(fields.requestID!!, fields.args, fields.kwargs, fields.options!!)
        }
    }

    val requestID: Long
        get() = yieldFields.requestID

    val args: List<Any>?
        get() = yieldFields.args

    val kwargs: Map<String, Any>?
        get() = yieldFields.kwargs

    val options: Map<String, Any>
        get() = yieldFields.options

    val payload: ByteArray?
        get() = yieldFields.payload

    val payloadSerializer: Int
        get() = yieldFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = yieldFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, requestID, options)

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
