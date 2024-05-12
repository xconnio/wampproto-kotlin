package io.xconn.messages

interface Message {
    fun marshal(): Array<Any>

    fun type(): Int
}
