package io.xconn.wampproto.messages

interface IAuthenticateFields {
    val signature: String
    val extra: Map<String, Any>
}

class AuthenticateFields(
    private val _signature: String,
    private val _extra: Map<String, Any>,
) : IAuthenticateFields {
    override val signature: String get() = _signature
    override val extra: Map<String, Any> get() = _extra
}

class Authenticate : Message {
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

    private var authenticateFields: IAuthenticateFields

    constructor(signature: String, extra: Map<String, Any>) {
        authenticateFields = AuthenticateFields(signature, extra)
    }

    constructor(fields: AuthenticateFields) {
        this.authenticateFields = fields
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
