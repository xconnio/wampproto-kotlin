package io.xconn.wampproto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SessionScopeIDGeneratorTest {
    @Test
    fun testGenerateSessionID() {
        val sessionID = generateSessionID()
        assertTrue(sessionID in 0 until ID_MAX, "Session ID should be within the valid range.")
    }

    @Test
    fun testSessionScopeIDGeneratorIncrementsCorrectly() {
        val generator = SessionScopeIDGenerator()

        // Generate a few IDs and check they are incrementing
        val firstID = generator.next()
        val secondID = generator.next()
        val thirdID = generator.next()

        assertEquals(firstID + 1, secondID, "The second ID should be incremented by 1 from the first.")
        assertEquals(secondID + 1, thirdID, "The third ID should be incremented by 1 from the second.")
    }

    @Test
    fun testSessionScopeIDGeneratorResetsAfterMaxScope() {
        val generator = SessionScopeIDGenerator()

        // Set ID to just below the MAX_SCOPE to test boundary condition
        generator.id = ID_MAX - 1

        val idAtMax = generator.next()
        val idAfterReset = generator.next()

        assertEquals(ID_MAX, idAtMax, "ID should be at MAX_SCOPE.")
        assertEquals(1, idAfterReset, "ID should reset to 1 after reaching MAX_SCOPE.")
    }
}
