package io.xconn.wampproto.messages

interface IGoodbyeFields {
    val details: Map<String, Any>
    val reason: String
}

class GoodbyeFields(
    override val details: Map<String, Any>,
    override val reason: String,
) : IGoodbyeFields

class Goodbye : Message {
    private var goodbyeFields: IGoodbyeFields

    constructor(details: Map<String, Any>, reason: String) {
        goodbyeFields = GoodbyeFields(details, reason)
    }

    constructor(fields: GoodbyeFields) {
        goodbyeFields = fields
    }

    companion object {
        const val TYPE = 6
        const val TEXT = "GOODBYE"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateDetails,
                        2 to ::validateReason,
                    ),
            )

        fun parse(message: List<Any>): Goodbye {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Goodbye(fields.details!!, fields.reason!!)
        }
    }

    val details: Map<String, Any>
        get() = goodbyeFields.details

    val reason: String
        get() = goodbyeFields.reason

    override fun marshal(): List<Any> {
        return listOf(TYPE, details, reason)
    }

    override fun type(): Int {
        return TYPE
    }
}
