package io.xconn.wampproto.messages

interface IWelcomeFields {
    val sessionID: Long
    val roles: Map<String, Any>
    val authID: String
    val authRole: String
    val authMethod: String
    val authExtra: Map<String, Any>
}

class WelcomeFields(
    private val _sessionID: Long,
    private val _roles: Map<String, Any>,
    private val _authid: String,
    private val _authRole: String,
    private val _authmethod: String,
    private val _authextra: Map<String, Any> = emptyMap(),
) : IWelcomeFields {
    override val sessionID: Long get() = _sessionID
    override val roles: Map<String, Any> get() = _roles
    override val authID: String get() = _authid
    override val authRole: String get() = _authRole
    override val authMethod: String get() = _authmethod
    override val authExtra: Map<String, Any> get() = _authextra
}

class Welcome : Message {
    companion object {
        const val TYPE = 2
        const val TEXT = "WELCOME"

        private val validationSpec =
            ValidationSpec(
                minLength = 3,
                maxLength = 3,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateSessionID,
                        2 to ::validateDetails,
                    ),
            )

        fun parse(msg: List<Any>): Message {
            val fields = validateMessage(msg, TYPE, validationSpec)

            val roles = validateRolesOrRaise(fields.details?.get("roles"), TEXT)

            val authid = validateStringOrRaise(fields.details?.get("authid"), TEXT, "authid")
            val authRole = validateStringOrRaise(fields.details?.get("authrole"), TEXT, "authrole")
            val authMethod = validateStringOrRaise(fields.details?.get("authmethod"), TEXT, "authmethod")

            val authExtra =
                fields.details?.get("authextra")?.let {
                    validateMapOrRaise(it, TEXT, "authextra")
                }

            return Welcome(fields.sessionID!!, roles, authid, authRole, authMethod, authExtra)
        }
    }

    private var welcomeFields: IWelcomeFields

    constructor(
        sessionID: Long,
        roles: Map<String, Any>,
        authid: String,
        authRole: String,
        authMethod: String,
        authExtra: Map<String, Any>? = null,
    ) {
        welcomeFields = WelcomeFields(sessionID, roles, authid, authRole, authMethod, authExtra ?: emptyMap())
    }

    constructor(fields: WelcomeFields) {
        this.welcomeFields = fields
    }

    val sessionID: Long
        get() = welcomeFields.sessionID

    val roles: Map<String, Any>
        get() = welcomeFields.roles

    val authID: String
        get() = welcomeFields.authID

    val authRole: String
        get() = welcomeFields.authRole

    val authMethod: String
        get() = welcomeFields.authMethod

    val authExtra: Map<String, Any>
        get() = welcomeFields.authExtra

    override fun marshal(): List<Any> {
        val details = HashMap<String, Any>()
        details["roles"] = roles
        details["authid"] = authID
        details["authrole"] = authRole
        details["authmethod"] = authMethod
        details["authextra"] = authExtra

        return listOf(TYPE, sessionID, details)
    }

    override fun type(): Int {
        return TYPE
    }
}
