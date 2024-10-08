package io.xconn.wampproto.messages

data class ValidationSpec(
    val minLength: Int,
    val maxLength: Int,
    val message: String,
    val spec: Map<Int, (List<Any>, Int, Fields, String) -> String?>,
)
