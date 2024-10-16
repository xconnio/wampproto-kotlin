package io.xconn.wampproto

import kotlin.random.Random

const val ID_MAX = 1L shl 53

fun generateSessionID(): Long {
    return Random.nextLong(ID_MAX)
}

class SessionScopeIDGenerator {
    var id: Long = 0

    fun next(): Long {
        if (id == ID_MAX) {
            id = 0
        }

        id += 1
        return id
    }
}
