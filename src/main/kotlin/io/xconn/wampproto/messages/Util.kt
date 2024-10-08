package io.xconn.wampproto.messages

import io.xconn.wampproto.ProtocolError

val allowedRoles: Set<String> = setOf("callee", "caller", "publisher", "subscriber", "dealer", "broker")

data class Fields(
    var requestID: Long? = null,
    var uri: String? = null,
    var args: List<Any>? = null,
    var kwargs: Map<String, Any>? = null,
    var sessionID: Long? = null,
    var realm: String? = null,
    var authID: String? = null,
    var authrole: String? = null,
    var authmethod: String? = null,
    var authmethods: List<String>? = null,
    var authextra: Map<String, Any>? = null,
    var roles: Map<String, Any>? = null,
    var messageType: Int? = null,
    var signature: String? = null,
    var reason: String? = null,
    var topic: String? = null,
    var extra: Map<String, Any>? = null,
    var options: Map<String, Any>? = null,
    var details: Map<String, Any>? = null,
    var subscriptionID: Long? = null,
    var publicationID: Long? = null,
    var registrationID: Long? = null,
)

fun sanityCheck(wampMessage: List<Any>, minLength: Int, maxLength: Int, expectedID: Int, name: String) {
    if (wampMessage.size < minLength) {
        throw ProtocolError("invalid message length ${wampMessage.size}, must be at least $minLength")
    }

    if (wampMessage.size > maxLength) {
        throw ProtocolError("invalid message length ${wampMessage.size}, must be at most $maxLength")
    }

    val messageID = wampMessage[0]
    if (messageID != expectedID) {
        throw ProtocolError("invalid message id $messageID for $name, expected $expectedID")
    }
}

fun invalidDataTypeError(message: String, index: Int, expectedType: Class<*>, actualType: String): String {
    return "$message: value at index $index must be of type '${expectedType.simpleName}' but was '$actualType'"
}

fun validateString(value: Any, index: Int, message: String): String? {
    if (value !is String) {
        return invalidDataTypeError(
            message = message,
            index = index,
            expectedType = String::class.java,
            actualType = value::class.java.simpleName,
        )
    }

    return null
}

fun validateRealm(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.realm = msg[index] as String

    return null
}

fun validateMap(value: Any, index: Int, message: String): String? {
    if (value !is Map<*, *>) {
        return invalidDataTypeError(
            message = message,
            index = index,
            expectedType = Map::class.java,
            actualType = value::class.java.simpleName,
        )
    }

    return null
}

fun validateRolesOrRaise(roles: Any?, errorMsg: String): Map<String, Any> {
    if (roles == null) {
        throw ProtocolError("roles cannot be null for $errorMsg")
    }

    if (roles !is Map<*, *>) {
        throw ProtocolError("roles must be of type map for $errorMsg but was ${roles::class.java.simpleName}")
    }

    for (role in roles.keys) {
        if (role !is String || !allowedRoles.contains(role)) {
            throw ProtocolError("invalid role '$role' in 'roles' details for $errorMsg")
        }
    }

    return (roles as? Map<String, Any>)
        ?: throw ProtocolError("Failed to cast roles to Map<String, Any>")
}

fun validateListOrRaise(list: Any?, errorMsg: String, field: String): List<Any> {
    if (list == null) {
        throw ProtocolError("$field cannot be null for $errorMsg")
    }

    if (list !is List<*>) {
        throw ProtocolError("$field must be of type list for $errorMsg")
    }

    return (list as? List<Any>) ?: throw ProtocolError("Failed to cast $field to List<Any>")
}

fun validateMapOrRaise(map: Any?, errorMsg: String, field: String): Map<String, Any> {
    if (map == null) {
        throw ProtocolError("$field cannot be null for $errorMsg")
    }

    if (map !is Map<*, *>) {
        throw ProtocolError("$field must be of type map for $errorMsg")
    }

    return (map as? Map<String, Any>)
        ?: throw ProtocolError("Failed to cast $field to Map<String, Any>")
}

fun validateStringOrRaise(string: Any?, errorMsg: String, field: String): String {
    if (string == null) {
        throw ProtocolError("$field cannot be null for $errorMsg")
    }

    if (string !is String) {
        throw ProtocolError("$field must be of type string for $errorMsg")
    }

    return string
}

fun validateDetails(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateMap(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.details = ((msg[index] as Map<*, *>).mapKeys { it.key.toString() } as? Map<String, Any>)
        ?: throw ProtocolError("Failed to cast details to Map<String, Any>")

    return null
}

fun validateMessage(msg: List<Any>, type: Int, valSpec: ValidationSpec): Fields {
    sanityCheck(msg, valSpec.minLength, valSpec.maxLength, type, valSpec.message)

    val errors = mutableListOf<String>()
    val f = Fields()
    valSpec.spec.forEach { (idx, func) ->
        val error = func(msg, idx, f, valSpec.message)
        if (error != null) {
            errors.add(error)
        }
    }

    if (errors.isNotEmpty()) {
        throw ProtocolError(errors.joinToString(", "))
    }

    return f
}
