package io.xconn.wampproto.messages

interface Message {
    fun marshal(): Array<Any>

    fun type(): Int
}
