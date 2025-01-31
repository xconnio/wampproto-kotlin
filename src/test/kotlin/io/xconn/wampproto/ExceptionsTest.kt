package io.xconn.wampproto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExceptionsTest {
    @Nested
    inner class ApplicationError {
        @Test
        fun messageOnly() {
            val error = ApplicationError("Unexpected error")
            assertEquals("Unexpected error", error.toString())
        }

        @Test
        fun withArgs() {
            val error = ApplicationError("Authentication failed", listOf("username", "password"))
            assertEquals("Authentication failed: username, password", error.toString())
        }

        @Test
        fun withKWArgs() {
            val error = ApplicationError("Permission denied", kwargs = mapOf("user" to "admin", "role" to "guest"))
            assertEquals("Permission denied: user=admin, role=guest", error.toString())
        }

        @Test
        fun withArgsAndKWArgs() {
            val error =
                ApplicationError(
                    "Something went wrong",
                    listOf(42, "test"),
                    mapOf("code" to 500, "reason" to "Internal Server error"),
                )
            assertEquals("Something went wrong: 42, test: code=500, reason=Internal Server error", error.toString())
        }

        @Test
        fun emptyArgsAndKWArgs() {
            val error = ApplicationError("Some error", emptyList(), emptyMap())
            assertEquals("Some error", error.toString())
        }
    }
}
