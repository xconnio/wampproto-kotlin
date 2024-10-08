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

    @Nested
    inner class ValidateInt {
        @Test
        fun validInt() {
            val error = validateInt(123, 0, "Test")
            assertNull(error)
        }

        @Test
        fun invalidInt() {
            val error = validateInt("string_value", 0, "Invalid Test")
            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'int' but was 'class java.lang.String'",
                error,
            )
        }
    }

    @Nested
    inner class ValidateID {
        @Test
        fun validIntID() {
            val error = validateID(123, 0, "Test")
            assertNull(error)
        }

        @Test
        fun validLongID() {
            val error = validateID(123L, 0, "Test")
            assertNull(error)
        }

        @Test
        fun invalidType() {
            val error = validateID("string_value", 0, "Invalid Test")
            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'long' but was 'class java.lang.String'",
                error,
            )
        }

        @Test
        fun outOfRangeID() {
            val error = validateID(9007199254740992, 0, "Out of Range Test")
            assertNotNull(error)
            assertEquals(
                "Out of Range Test: value at index 0 must be between '$MIN_ID' and '$MAX_ID' but was '9007199254740992'",
                error,
            )
        }
    }

    @Nested
    inner class ValidateSessionID {
        @Test
        fun validSessionID() {
            val message = listOf(123L)
            val fields = Fields()

            val error = validateSessionID(message, 0, fields, "Test")

            assertNull(error)
            assertNotNull(fields.sessionID)
            assertEquals(123L, fields.sessionID)
        }

        @Test
        fun invalidSessionID() {
            val message = listOf("invalid_session_id")
            val fields = Fields()

            val error = validateSessionID(message, 0, fields, "Invalid Test")

            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'long' but was 'class java.lang.String'",
                error,
            )
        }

        @Test
        fun sessionIDOutOfRange() {
            val message = listOf(MAX_ID + 1)
            val fields = Fields()

            val error = validateSessionID(message, 0, fields, "Out of Range Test")

            assertNotNull(error)
            assertEquals(
                "Out of Range Test: value at index 0 must be between '1' and '9007199254740991' but was '${MAX_ID + 1}'",
                error,
            )
        }
    }

    @Nested
    inner class ValidateAuthMethod {
        @Test
        fun validAuthMethod() {
            val message = listOf("valid_auth_method")
            val fields = Fields()

            val error = validateAuthMethod(message, 0, fields, "Test")

            assertNull(error)
            assertEquals("valid_auth_method", fields.authmethod)
        }

        @Test
        fun invalidAuthMethod() {
            val message = listOf(123)
            val fields = Fields()

            val error = validateAuthMethod(message, 0, fields, "Invalid Test")

            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'String' but was 'Integer'",
                error,
            )
        }
    }

    @Nested
    inner class ValidateExtra {
        @Test
        fun validExtra() {
            val message = listOf(mapOf("key" to "value"))
            val fields = Fields()

            val error = validateExtra(message, 0, fields, "Test")

            assertNull(error)
            assertEquals(mapOf("key" to "value"), fields.extra)
        }

        @Test
        fun invalidExtra() {
            val message = listOf("invalid_extra")
            val fields = Fields()

            val error = validateExtra(message, 0, fields, "Invalid Test")

            assertNotNull(error)
            assertEquals(
                "Invalid Test: value at index 0 must be of type 'Map' but was 'String'",
                error,
            )
        }
    }
}
