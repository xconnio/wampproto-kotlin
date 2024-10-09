package io.xconn.wampproto.messages

interface Message {
    fun marshal(): List<Any>

    fun type(): Int
}

interface BinaryPayload {
    val payloadIsBinary: Boolean

    val payload: ByteArray?

    val payloadSerializer: Int
}
