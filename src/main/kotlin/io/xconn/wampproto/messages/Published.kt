package io.xconn.wampproto.messages

interface IPublishedFields {
    val requestID: Long
    val publicationID: Long
}

class PublishedFields(
    override val requestID: Long,
    override val publicationID: Long,
) : IPublishedFields

class Published : Message {
    private var publishedFields: IPublishedFields

    constructor(requestID: Long, publicationID: Long) {
        publishedFields = PublishedFields(requestID, publicationID)
    }

    constructor(fields: IPublishedFields) {
        publishedFields = fields
    }

    companion object {
        const val TYPE: Int = 17
        const val TEXT: String = "PUBLISHED"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validatePublicationID,
                    ),
            )

        fun parse(message: List<Any>): Published {
            val fields = validateMessage(message, TYPE, validationSpec)
            return Published(fields.requestID!!, fields.publicationID!!)
        }
    }

    val requestID: Long
        get() = publishedFields.requestID

    val publicationID: Long
        get() = publishedFields.publicationID

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, publicationID)
    }

    override fun type(): Int = TYPE
}
