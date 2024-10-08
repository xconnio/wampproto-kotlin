package io.xconn.wampproto.messages

interface Message {
    fun marshal(): List<Any>

    fun type(): Int
}
