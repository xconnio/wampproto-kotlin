package io.xconn.wampproto

class ApplicationError(
    override val message: String,
    val args: List<Any>? = null,
    val kwargs: Map<String, Any>? = null,
) : Exception(message) {
    override fun toString(): String {
        var errStr = message
        args?.takeIf { it.isNotEmpty() }?.let {
            errStr += ": " + it.joinToString(", ") { arg -> arg.toString() }
        }
        kwargs?.takeIf { it.isNotEmpty() }?.let {
            errStr += ": " + it.entries.joinToString(", ") { (key, value) -> "$key=$value" }
        }
        return errStr
    }
}

class ProtocolError(override val message: String) : Exception(message)

class SessionNotReady(override val message: String) : Exception(message)
