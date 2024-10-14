package io.xconn.wampproto.messages

import io.xconn.wampproto.ProtocolError

val allowedRoles: Set<String> = setOf("callee", "caller", "publisher", "subscriber", "dealer", "broker")

const val MIN_ID = 1

// Maximum WAMP ID value (2^53 - 1), supported by the browser
const val MAX_ID = 9007199254740991

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

fun invalidRangeError(message: String, index: Int, start: String, end: String, actual: String): String {
    return "$message: value at index $index must be between '$start' and '$end' but was '$actual'"
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

fun validateList(value: Any, index: Int, message: String): String? {
    if (value !is List<*>) {
        return invalidDataTypeError(
            message = message,
            index = index,
            expectedType = List::class.java,
            actualType = value::class.java.simpleName,
        )
    }

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

fun validateInt(value: Any, index: Int, message: String): String? {
    if (value !is Int) {
        return invalidDataTypeError(
            message = message,
            index = index,
            expectedType = Int::class.java,
            actualType = value::class.java.simpleName,
        )
    }

    return null
}

fun validateID(value: Any, index: Int, message: String): String? {
    val numValue =
        when (value) {
            is Int -> value.toLong()
            is Long -> value
            else -> return invalidDataTypeError(
                message = message,
                index = index,
                expectedType = Long::class.java,
                actualType = value::class.java.simpleName,
            )
        }

    if (numValue < MIN_ID || numValue > MAX_ID) {
        return invalidRangeError(
            message = message,
            index = index,
            start = MIN_ID.toString(),
            end = MAX_ID.toString(),
            actual = numValue.toString(),
        )
    }

    return null
}

fun validateSessionID(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateID(msg[index], index, message)
    if (error != null) {
        return error
    }

    when (msg[index]) {
        is Int -> fields.sessionID = (msg[index] as Int).toLong()
        is Long -> fields.sessionID = msg[index] as Long
    }

    return null
}

fun validateRequestID(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateID(msg[index], index, message)
    if (error != null) {
        return error
    }

    when (msg[index]) {
        is Int -> fields.requestID = (msg[index] as Int).toLong()
        is Long -> fields.requestID = msg[index] as Long
    }

    return null
}

fun validateRegistrationID(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateID(msg[index], index, message)
    if (error != null) {
        return error
    }

    when (msg[index]) {
        is Int -> fields.registrationID = (msg[index] as Int).toLong()
        is Long -> fields.registrationID = msg[index] as Long
    }

    return null
}

fun validateSubscriptionID(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateID(msg[index], index, message)
    if (error != null) {
        return error
    }

    when (msg[index]) {
        is Int -> fields.subscriptionID = (msg[index] as Int).toLong()
        is Long -> fields.subscriptionID = msg[index] as Long
    }

    return null
}

fun validateUri(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.uri = msg[index] as String

    return null
}

fun validateTopic(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.topic = msg[index] as String

    return null
}

fun validateMessageType(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateInt(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.messageType = msg[index] as Int

    return null
}

fun validateAuthMethod(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.authmethod = msg[index] as String

    return null
}

fun validateExtra(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateMap(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.extra = (msg[index] as Map<String, Any> as? Map<String, Any>)
        ?: throw ProtocolError("Failed to cast details to Map<String, Any>")

    return null
}

fun validateSignature(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.signature = msg[index] as String

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

fun validateOptions(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateMap(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.options = (msg[index] as? Map<String, Any>)
        ?: throw ProtocolError("Failed to cast details to Map<String, Any>")

    return null
}

fun validateReason(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    val error = validateString(msg[index], index, message)
    if (error != null) {
        return error
    }
    fields.reason = msg[index] as String

    return null
}

fun validateArgs(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    if (msg.size > index) {
        val error = validateList(msg[index], index, message)
        if (error != null) {
            return error
        }
        fields.args = (msg[index] as? List<Any>) ?: throw ProtocolError("Failed to cast details to List<Any>")
    }

    return null
}

fun validateKwargs(msg: List<Any>, index: Int, fields: Fields, message: String): String? {
    if (msg.size > index) {
        val error = validateMap(msg[index], index, message)
        if (error != null) {
            return error
        }
        fields.kwargs = (msg[index] as? Map<String, Any>) ?: throw ProtocolError("Failed to cast details to Map<String, Any>")
    }

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
