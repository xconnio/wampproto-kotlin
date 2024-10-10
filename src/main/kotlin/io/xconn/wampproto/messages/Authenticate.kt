package io.xconn.wampproto.messages

interface IAuthenticateFields {
    val signature: String
    val extra: Map<String, Any>
}

class AuthenticateFields(
    override val signature: String,
    override val extra: Map<String, Any>,
) : IAuthenticateFields

class Authenticate : Message {
    private var authenticateFields: IAuthenticateFields

    constructor(signature: String, extra: Map<String, Any>) {
        authenticateFields = AuthenticateFields(signature, extra)
    }

    constructor(fields: AuthenticateFields) {
        this.authenticateFields = fields
    }

    companion object {
        const val TYPE = 5
        const val TEXT = "AUTHENTICATE"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateSignature,
                        2 to ::validateExtra,
                    ),
            )

        fun parse(message: List<Any>): Authenticate {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Authenticate(fields.signature!!, fields.extra!!)
        }
    }

    val signature: String
        get() = authenticateFields.signature

    val extra: Map<String, Any>
        get() = authenticateFields.extra

    override fun marshal(): List<Any> {
        return listOf(TYPE, signature, extra)
    }

    override fun type(): Int {
        return TYPE
    }
}
