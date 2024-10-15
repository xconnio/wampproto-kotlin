package io.xconn.wampproto.messages

interface IHelloFields {
    val realm: String
    val roles: Map<String, Any>
    val authID: String
    val authMethods: List<Any>
    val authExtra: Map<String, Any>
}

class HelloFields(
    override val realm: String,
    override val roles: Map<String, Any>,
    override val authID: String,
    override val authMethods: List<Any>,
    override val authExtra: Map<String, Any> = emptyMap(),
) : IHelloFields

class Hello : Message {
    private var helloFields: IHelloFields

    constructor(
        realm: String,
        roles: Map<String, Any>,
        authid: String,
        authMethods: List<Any>,
        authExtra: Map<String, Any>? = null,
    ) {
        helloFields = HelloFields(realm, roles, authid, authMethods, authExtra ?: emptyMap())
    }

    constructor(fields: HelloFields) {
        helloFields = fields
    }

    companion object {
        const val TYPE = 1
        const val TEXT = "HELLO"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRealm,
                        2 to ::validateDetails,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            val roles = validateRolesOrRaise(fields.details?.get("roles"), TEXT)

            val authid =
                fields.details?.get("authid")?.let {
                    validateStringOrRaise(it, TEXT, "authid")
                } ?: ""

            val authMethods =
                fields.details?.get("authmethods")?.let {
                    validateListOrRaise(it, TEXT, "authmethods")
                } ?: listOf()

            val authExtra =
                fields.details?.get("authextra")?.let {
                    validateMapOrRaise(it, TEXT, "authextra")
                }

            return Hello(fields.realm!!, roles, authid, authMethods, authExtra)
        }
    }

    val realm: String
        get() = helloFields.realm

    val roles: Map<String, Any>
        get() = helloFields.roles

    val authID: String
        get() = helloFields.authID

    val authMethods: List<Any>
        get() = helloFields.authMethods

    val authExtra: Map<String, Any>
        get() = helloFields.authExtra

    override fun marshal(): List<Any> {
        val details = HashMap<String, Any>()
        details["roles"] = roles

        if (authID.isNotEmpty()) details["authid"] = authID
        if (authMethods.isNotEmpty()) details["authmethods"] = authMethods
        if (authExtra.isNotEmpty()) details["authextra"] = authExtra

        return listOf(TYPE, realm, details)
    }

    override fun type(): Int {
        return TYPE
    }
}
