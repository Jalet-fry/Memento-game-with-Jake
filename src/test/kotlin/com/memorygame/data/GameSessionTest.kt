package com.memorygame.data

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для класса GameSession
 */
class GameSessionTest {
    
    @Test
    fun testGameSessionCreation() {
        val session = GameSession(
            playerName = "TestPlayer",
            difficulty = 4,
            time = 120,
            attempts = 15,
            matchedPairs = 8,
            date = System.currentTimeMillis(),
            won = true,
            rating = 5
        )
        
        assertEquals("TestPlayer", session.playerName)
        assertEquals(4, session.difficulty)
        assertEquals(120, session.time)
        assertEquals(15, session.attempts)
        assertEquals(8, session.matchedPairs)
        assertTrue(session.won)
        assertEquals(5, session.rating)
    }
    
    @Test
    fun testGetFormattedTime() {
        val session = GameSession(
            playerName = "TestPlayer",
            difficulty = 4,
            time = 125,
            attempts = 15,
            matchedPairs = 8,
            date = System.currentTimeMillis(),
            won = true,
            rating = 5
        )
        
        assertEquals("02:05", session.getFormattedTime())
    }
    
    @Test
    fun testGetRatingStars() {
        val session = GameSession(
            playerName = "TestPlayer",
            difficulty = 4,
            time = 120,
            attempts = 15,
            matchedPairs = 8,
            date = System.currentTimeMillis(),
            won = true,
            rating = 3
        )
        
        assertEquals("⭐⭐⭐", session.getRatingStars())
    }
    
    @Test
    fun testGetDifficultyString() {
        val session = GameSession(
            playerName = "TestPlayer",
            difficulty = 6,
            time = 120,
            attempts = 15,
            matchedPairs = 18,
            date = System.currentTimeMillis(),
            won = true,
            rating = 4
        )
        
        assertEquals("6x6", session.getDifficultyString())
    }
}

