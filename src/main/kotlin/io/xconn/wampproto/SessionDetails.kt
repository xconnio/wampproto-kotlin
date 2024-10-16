package io.xconn.wampproto

class SessionDetails(
    val sessionID: Long,
    val realm: String,
    val authid: String,
    val authrole: String,
)
