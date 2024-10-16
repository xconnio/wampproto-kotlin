package io.xconn.wampproto

class ProtocolError(override val message: String) : Exception(message)

class SessionNotReady(override val message: String) : Exception(message)
