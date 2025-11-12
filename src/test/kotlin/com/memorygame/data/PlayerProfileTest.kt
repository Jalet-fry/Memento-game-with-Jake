package com.memorygame.data

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для класса PlayerProfile
 */
class PlayerProfileTest {
    
    @Test
    fun testPlayerProfileCreation() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        assertEquals("TestPlayer", profile.name)
        assertEquals(0, profile.totalGames)
        assertEquals(0, profile.wonGames)
        assertEquals(Int.MAX_VALUE, profile.bestTime)
        assertEquals(0, profile.totalAttempts)
        assertEquals(0, profile.totalMatches)
        assertTrue(profile.getAchievementsSet().isEmpty())
        assertTrue(profile.getGameSessionsList().isEmpty())
    }
    
    @Test
    fun testAddGameSession() {
        val profile = PlayerProfile(name = "TestPlayer")
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
        
        profile.addGameSession(session)
        
        assertEquals(1, profile.totalGames)
        assertEquals(1, profile.wonGames)
        assertEquals(120, profile.bestTime)
        assertEquals(15, profile.totalAttempts)
        assertEquals(8, profile.totalMatches)
        assertEquals(1, profile.getGameSessionsList().size)
    }
    
    @Test
    fun testGetWinRate() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        // Добавляем 2 выигранные игры из 3
        profile.addGameSession(createWonSession())
        profile.addGameSession(createWonSession())
        profile.addGameSession(createLostSession())
        
        val winRate = profile.getWinRate()
        assertEquals(66.67, winRate, 0.1)
    }
    
    @Test
    fun testGetAverageAttempts() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        profile.addGameSession(createWonSession(attempts = 10))
        profile.addGameSession(createWonSession(attempts = 20))
        
        val averageAttempts = profile.getAverageAttempts()
        assertEquals(15.0, averageAttempts, 0.1)
    }
    
    @Test
    fun testGetBestTimeFormatted() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        assertEquals("Нет", profile.getBestTimeFormatted())
        
        profile.addGameSession(createWonSession(time = 125))
        assertEquals("02:05", profile.getBestTimeFormatted())
    }
    
    @Test
    fun testGetBestTimesByDifficulty() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        profile.addGameSession(createWonSession(difficulty = 4, time = 120))
        profile.addGameSession(createWonSession(difficulty = 4, time = 100))
        profile.addGameSession(createWonSession(difficulty = 6, time = 200))
        
        val bestTimes = profile.getBestTimesByDifficulty()
        assertEquals(100, bestTimes[4])
        assertEquals(200, bestTimes[6])
    }
    
    @Test
    fun testAchievements() {
        val profile = PlayerProfile(name = "TestPlayer")
        
        // Первая игра
        profile.addGameSession(createWonSession())
        assertTrue(profile.getAchievementsSet().contains("🎮 Первая игра!"))
    }
    
    private fun createWonSession(
        difficulty: Int = 4,
        time: Int = 120,
        attempts: Int = 15
    ): GameSession {
        return GameSession(
            playerName = "TestPlayer",
            difficulty = difficulty,
            time = time,
            attempts = attempts,
            matchedPairs = 8,
            date = System.currentTimeMillis(),
            won = true,
            rating = 5
        )
    }
    
    private fun createLostSession(): GameSession {
        return GameSession(
            playerName = "TestPlayer",
            difficulty = 4,
            time = 300,
            attempts = 30,
            matchedPairs = 4,
            date = System.currentTimeMillis(),
            won = false,
            rating = 2
        )
    }
}

