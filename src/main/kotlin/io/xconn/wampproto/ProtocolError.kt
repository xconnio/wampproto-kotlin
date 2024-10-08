package io.xconn.wampproto

class ProtocolError(override val message: String) : Exception(message)
