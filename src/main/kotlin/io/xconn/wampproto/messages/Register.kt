package io.xconn.wampproto.messages

interface IRegisterFields {
    val requestID: Long
    val uri: String
    val options: Map<String, Any>
}

class RegisterFields(
    override val requestID: Long,
    override val uri: String,
    override val options: Map<String, Any> = emptyMap(),
) : IRegisterFields

class Register : Message {
    private var registerFields: IRegisterFields

    constructor(
        requestID: Long,
        uri: String,
        options: Map<String, Any>? = null,
    ) {
        registerFields = RegisterFields(requestID, uri, options ?: emptyMap())
    }

    constructor(fields: RegisterFields) {
        registerFields = fields
    }

    companion object {
        const val TYPE = 64
        const val TEXT = "REGISTER"

        private val validationSpec =
            ValidationSpec(
                minLength = 4,
                maxLength = 4,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateOptions,
                        3 to ::validateUri,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            return Register(fields.requestID!!, fields.uri!!, fields.options!!)
        }
    }

    val requestID: Long
        get() = registerFields.requestID

    val uri: String
        get() = registerFields.uri

    val options: Map<String, Any>
        get() = registerFields.options

    override fun marshal(): List<Any> {
        return listOf(TYPE, requestID, options, uri)
    }

    override fun type(): Int {
        return TYPE
    }
}
