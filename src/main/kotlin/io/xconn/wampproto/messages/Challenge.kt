package io.xconn.wampproto.messages

interface IChallengeFields {
    val authMethod: String
    val extra: Map<String, Any>
}

class ChallengeFields(
    override val authMethod: String,
    override val extra: Map<String, Any>,
) : IChallengeFields

class Challenge : Message {
    private var challengeFields: IChallengeFields

    constructor(authMethod: String, extra: Map<String, Any>) {
        challengeFields = ChallengeFields(authMethod, extra)
    }

    constructor(fields: ChallengeFields) {
        this.challengeFields = fields
    }

    companion object {
        const val TYPE = 4
        const val TEXT = "CHALLENGE"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateAuthMethod,
                        2 to ::validateExtra,
                    ),
            )

        fun parse(message: List<Any>): Challenge {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Challenge(fields.authmethod!!, fields.extra!!)
        }
    }

    val authMethod: String
        get() = challengeFields.authMethod

    val extra: Map<String, Any>
        get() = challengeFields.extra

    override fun marshal(): List<Any> {
        return listOf(TYPE, authMethod, extra)
    }

    override fun type(): Int {
        return TYPE
    }
}
