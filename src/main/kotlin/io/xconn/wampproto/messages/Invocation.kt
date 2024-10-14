package io.xconn.wampproto.messages

interface IInvocationFields : BinaryPayload {
    val requestID: Long
    val registrationID: Long
    val args: List<Any>?
    val kwargs: Map<String, Any>?
    val details: Map<String, Any>
}

class InvocationFields(
    override val requestID: Long,
    override val registrationID: Long,
    override val args: List<Any>?,
    override val kwargs: Map<String, Any>?,
    override val details: Map<String, Any> = emptyMap(),
    override val payload: ByteArray? = null,
    override val payloadSerializer: Int = 0,
    override val payloadIsBinary: Boolean = payloadSerializer != 0,
) : IInvocationFields

class Invocation : Message {
    private var invocationFields: IInvocationFields

    constructor(
        requestID: Long,
        registrationID: Long,
        args: List<Any>? = null,
        kwargs: Map<String, Any>? = null,
        details: Map<String, Any> = emptyMap(),
    ) {
        invocationFields = InvocationFields(requestID, registrationID, args, kwargs, details)
    }

    constructor(fields: IInvocationFields) {
        invocationFields = fields
    }

    companion object {
        const val TYPE: Int = 68
        const val TEXT: String = "INVOCATION"

        private val validationSpec =
            ValidationSpec(
                minLength = 4,
                maxLength = 6,
                message = TEXT,
                spec =
                    mapOf(
                        1 to ::validateRequestID,
                        2 to ::validateRegistrationID,
                        3 to ::validateDetails,
                        4 to ::validateArgs,
                        5 to ::validateKwargs,
                    ),
            )

        fun parse(message: List<Any>): Invocation {
            val fields = validateMessage(message, TYPE, validationSpec)

            return Invocation(fields.requestID!!, fields.registrationID!!, fields.args, fields.kwargs, fields.details!!)
        }
    }

    val requestID: Long
        get() = invocationFields.requestID

    val registrationID: Long
        get() = invocationFields.registrationID

    val args: List<Any>?
        get() = invocationFields.args

    val kwargs: Map<String, Any>?
        get() = invocationFields.kwargs

    val details: Map<String, Any>
        get() = invocationFields.details

    val payload: ByteArray?
        get() = invocationFields.payload

    val payloadSerializer: Int
        get() = invocationFields.payloadSerializer

    val payloadIsBinary: Boolean
        get() = invocationFields.payloadIsBinary

    override fun marshal(): List<Any> {
        val message = mutableListOf(TYPE, requestID, registrationID, details)

        args?.let { message.add(it) }
        kwargs?.let {
            if (args == null) {
                message.add(emptyList<Any>())
            }
            message.add(it)
        }

        return message
    }

    override fun type(): Int = TYPE
}
