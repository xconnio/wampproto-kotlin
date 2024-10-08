package io.xconn.wampproto.messages

import io.xconn.wampproto.ProtocolError
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UtilTest {
    @Nested
    inner class SanityCheck {
        @Test
        fun validMessage() {
            val message = listOf(1, "test", "value")
            assertDoesNotThrow {
                sanityCheck(message, 2, 5, 1, "TEST")
            }
        }

        @Test
        fun invalidMessage() {
            val message = listOf(1)
            val exception =
                assertThrows(ProtocolError::class.java) {
                    sanityCheck(message, 2, 5, 1, "TEST")
                }
            assertEquals("invalid message length 1, must be at least 2", exception.message)
        }

        @Test
        fun invalidMessageID() {
            val message = listOf(2, "test")
            val exception =
                assertThrows(ProtocolError::class.java) {
                    sanityCheck(message, 2, 5, 1, "TEST")
                }
            assertEquals("invalid message id 2 for TEST, expected 1", exception.message)
        }
    }

    @Nested
    inner class ValidateString {
        @Test
        fun validString() {
            val error = validateString("valid", 0, "Test")
            assertNull(error)
        }

        @Test
        fun invalidString() {
            val error = validateString(123, 0, "Invalid Test")
            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'String' but was 'Integer'",
                error,
            )
        }
    }

    @Nested
    inner class ValidateRealm {
        @Test
        fun validRealm() {
            val fields = Fields()
            val message = listOf("validRealm")
            val error = validateRealm(message, 0, fields, "Test")
            assertNull(error)
            assertEquals("validRealm", fields.realm)
        }

        @Test
        fun invalidRealm() {
            val fields = Fields()
            val message = listOf(123)
            val error = validateRealm(message, 0, fields, "Test")
            assertNotNull(error)
            assertEquals("Test: value at index 0 must be of type 'String' but was 'Integer'", error)
        }
    }

    @Nested
    inner class ValidateRolesOrRaise {
        @Test
        fun validRoles() {
            val roles = mapOf("callee" to mapOf<String, Any>(), "caller" to mapOf<String, Any>())
            assertDoesNotThrow {
                validateRolesOrRaise(roles, "Test")
            }
        }

        @Test
        fun invalidRoles() {
            val roles = mapOf("invalidRole" to mapOf<String, Any>())
            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateRolesOrRaise(roles, "Test")
                }
            assertEquals(
                "invalid role 'invalidRole' in 'roles' details for Test",
                exception.message,
            )
        }
    }

    @Nested
    inner class ValidateListOrRaise {
        @Test
        fun validList() {
            val list = listOf("abc", 1, true)
            assertDoesNotThrow {
                validateListOrRaise(list, "Test", "Test")
            }
        }

        @Test
        fun nullList() {
            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateListOrRaise(null, "Test", "args")
                }
            assertEquals("args cannot be null for Test", exception.message)
        }

        @Test
        fun invalidValue() {
            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateListOrRaise("notAList", "Test", "args")
                }
            assertEquals("args must be of type list for Test", exception.message)
        }
    }

    @Nested
    inner class ValidateStringOrRaise {
        @Test
        fun validString() {
            assertDoesNotThrow {
                validateStringOrRaise("validAuthID", "Test", "authID")
            }
        }

        @Test
        fun nullString() {
            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateStringOrRaise(null, "Test", "authID")
                }
            assertEquals("authID cannot be null for Test", exception.message)
        }
    }

    @Nested
    inner class ValidateMapOrRaiseTests {
        @Test
        fun validMap() {
            val map: Any = mapOf("key1" to "value1", "key2" to 123)
            val result = validateMapOrRaise(map, "Test Error", "details")

            assertNotNull(result)
            assertEquals(2, result.size)
            assertEquals("value1", result["key1"])
            assertEquals(123, result["key2"])
        }

        @Test
        fun mapIsNull() {
            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateMapOrRaise(null, "Test Error", "details")
                }

            assertEquals("details cannot be null for Test Error", exception.message)
        }

        @Test
        fun mapIsNotAMap() {
            val nonMap: Any = "This is not a map"

            val exception =
                assertThrows(ProtocolError::class.java) {
                    validateMapOrRaise(nonMap, "Test Error", "details")
                }

            assertEquals("details must be of type map for Test Error", exception.message)
        }
    }

    @Nested
    inner class ValidateDetails {
        @Test
        fun validDetails() {
            val message = listOf(1, mapOf("key1" to "value1", "key2" to "value2"))
            val fields = Fields()

            val error = validateDetails(message, 1, fields, "Test")

            assertNull(error)
            assertNotNull(fields.details)
            assertEquals(2, fields.details?.size)
            assertEquals("value1", fields.details?.get("key1"))
            assertEquals("value2", fields.details?.get("key2"))
        }

        @Test
        fun invalidDetails() {
            val message = listOf(1, "invalid_details")
            val fields = Fields()

            val error = validateDetails(message, 1, fields, "Invalid Test")

            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 1 must be of type 'Map' but was 'String'",
                error,
            )
        }
    }
}
