package io.xconn.wampproto.messages

interface IEventFields : BinaryPayload {
    val subscriptionID: Long
    val publicationID: Long
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val details: Map<String, Any>
}

class EventFields(
    override val subscriptionID: Long,
    override val publicationID: Long,
    override val args: List<Any>? = null,
    override val kwargs: Map<String, Any>? = null,
    override val details: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IEventFields

class Event : Message {
    private var eventFields: IEventFields

    constructor(
        subscriptionID: Long,
        publicationID: Long,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        details: Map<String, Any> = emptyMap(),
    ) {
        eventFields = EventFields(subscriptionID, publicationID, args, kwargs, details)
    }

    constructor(fields: IEventFields) {
        eventFields = fields
    }

    companion object {
        const val TYPE: Int = 36
        const val TEXT: String = "EVENT"

        private val validationSpec =
            ValidationSpec(
                minLength = 4,
                maxLength = 6,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateSubscriptionID,
                        2 to ::validatePublicationID,
                        3 to ::validateDetails,
                        4 to ::validateArgs,
                        5 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Event {
            val fields = validateMessage(message, TYPE, validationSpec)
            return Event(
                fields.subscriptionID!!,
                fields.publicationID!!,
                fields.args,
                fields.kwargs,
                fields.details!!,
            )
        }
    }

    val subscriptionID: Long
        get() = eventFields.subscriptionID

    val publicationID: Long
        get() = eventFields.publicationID

    val args: List<Any>?
        get() = eventFields.args

    val kwargs: Map<String, Any>?
        get() = eventFields.kwargs

    val details: Map<String, Any>
        get() = eventFields.details

    val payload: ByteArray?
        get() = eventFields.payload

    val payloadSerializer: Int
        get() = eventFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = eventFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, subscriptionID, publicationID, details)

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
