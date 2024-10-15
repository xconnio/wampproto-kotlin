package io.xconn.wampproto.messages

interface IPublishFields : BinaryPayload {
    val requestID: Long
    val uri: String
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val options: Map<String, Any>
}

class PublishFields(
    override val requestID: Long,
    override val uri: String,
    override val args: List<Any>? = null,
    override val kwargs: Map<String, Any>? = null,
    override val options: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IPublishFields

class Publish : Message {
    private var publishFields: IPublishFields

    constructor(
        requestID: Long,
        uri: String,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        options: Map<String, Any> = emptyMap(),
    ) {
        publishFields = PublishFields(requestID, uri, args, kwargs, options)
    }

    constructor(fields: IPublishFields) {
        publishFields = fields
    }

    companion object {
        const val TYPE: Int = 16
        const val TEXT: String = "PUBLISH"

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

        fun parse(message: List<Any>): Publish {
            val fields = validateMessage(message, TYPE, validationSpec)
            return Publish(
                fields.requestID!!,
                fields.uri!!,
                fields.args,
                fields.kwargs,
                fields.options!!,
            )
        }
    }

    val requestID: Long
        get() = publishFields.requestID

    val uri: String
        get() = publishFields.uri

    val args: List<Any>?
        get() = publishFields.args

    val kwargs: Map<String, Any>?
        get() = publishFields.kwargs

    val options: Map<String, Any>
        get() = publishFields.options

    val payload: ByteArray?
        get() = publishFields.payload

    val payloadSerializer: Int
        get() = publishFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = publishFields.payloadIsBinary

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
