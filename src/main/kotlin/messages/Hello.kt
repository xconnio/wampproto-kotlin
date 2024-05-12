package io.xconn.messages

class Hello(
    val realm: String,
    val authid: String,
    val authMethods: Array<String>,
    val roles: Map<String, Any>,
    val authExtra: Map<String, Any>,
) : Message {
    companion object {
        const val TYPE = 1
        const val TEXT = "HELLO"

        fun parse(msg: Array<Any>): Message {
            val realm = msg[1] as String
            val details = msg[2] as HashMap<*, *>

            val authid = details["authid"] as String

            val authMethods: Array<String>
            if (details["authmethods"] is Array<*>) {
                authMethods = details["authmethods"] as Array<String>
            } else {
                authMethods = emptyArray()
            }

            val roles: HashMap<String, Any>
            if (details["roles"] is HashMap<*, *>) {
                roles = details["roles"] as HashMap<String, Any>
            } else {
                roles = HashMap()
            }

            val authExtra: HashMap<String, Any>
            if (details["authextra"] is HashMap<*, *>) {
                authExtra = details["authextra"] as HashMap<String, Any>
            } else {
                authExtra = HashMap()
            }

            return Hello(realm, authid, authMethods, roles, authExtra)
        }
    }

    override fun marshal(): Array<Any> {
        val details = HashMap<String, Any>()
        details["roles"] = roles

        if (authid.isNotEmpty()) details["authid"] = authid
        if (authMethods.isNotEmpty()) details["authmethods"] = authMethods
        if (authExtra.isNotEmpty()) details["authextra"] = authExtra

        return arrayOf(TYPE, realm, details)
    }

    override fun type(): Int {
        return TYPE
    }
}
