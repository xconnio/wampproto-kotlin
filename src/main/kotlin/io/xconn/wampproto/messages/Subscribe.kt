package io.xconn.wampproto.messages

interface ISubscribeFields {
    val requestID: Long
    val topic: String
    val options: Map<String, Any>
}

class SubscribeFields(
    override val requestID: Long,
    override val topic: String,
    override val options: Map<String, Any> = emptyMap(),
) : ISubscribeFields

class Subscribe : Message {
    private var subscribeFields: ISubscribeFields

    constructor(
        requestID: Long,
        topic: String,
        options: Map<String, Any>? = null,
    ) {
        subscribeFields = SubscribeFields(requestID, topic, options ?: emptyMap())
    }

    constructor(fields: SubscribeFields) {
        subscribeFields = fields
    }

    companion object {
        const val TYPE = 32
        const val TEXT = "SUBSCRIBE"

        private val validationSpec =
            ValidationSpec(
                minLength = 4,
                maxLength = 4,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateOptions,
                        3 to ::validateTopic,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Subscribe(fields.requestID!!, fields.topic!!, fields.options!!)
        }
    }

    val requestID: Long
        get() = subscribeFields.requestID

    val topic: String
        get() = subscribeFields.topic

    val options: Map<String, Any>
        get() = subscribeFields.options

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, options, topic)
    }

    override fun type(): Int {
        return TYPE
    }
}
