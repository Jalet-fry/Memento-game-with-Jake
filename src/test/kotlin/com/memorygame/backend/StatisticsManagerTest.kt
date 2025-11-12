package com.memorygame.backend

import com.memorygame.data.GameSession
import com.memorygame.data.PlayerProfile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.io.File

/**
 * Тесты для StatisticsManager
 */
class StatisticsManagerTest {
    
    private val testDataDir = File(System.getProperty("user.home"), ".memorygame_test")
    private val testStatisticsFile = File(testDataDir, "statistics.json")
    
    @BeforeEach
    fun setUp() {
        // Сбрасываем состояние StatisticsManager
        StatisticsManager.resetForTesting()
        
        // Очищаем тестовую директорию
        if (testDataDir.exists()) {
            testDataDir.deleteRecursively()
        }
        testDataDir.mkdirs()
        
        // Устанавливаем тестовые пути
        StatisticsManager.setTestPaths(testDataDir, testStatisticsFile)
    }
    
    @AfterEach
    fun tearDown() {
        // Сбрасываем состояние StatisticsManager
        StatisticsManager.resetForTesting()
        
        // Очищаем тестовую директорию после тестов
        if (testDataDir.exists()) {
            testDataDir.deleteRecursively()
        }
    }
    
    @Test
    fun testInitialize() {
        // Инициализация не нужна, так как пути уже установлены в setUp
        // Просто проверяем, что можем сохранить и загрузить
        assertTrue(StatisticsManager.saveStatistics())
        assertTrue(StatisticsManager.loadStatistics())
    }
    
    @Test
    fun testCreatePlayer() {
        val player = StatisticsManager.createPlayer("TestPlayer")
        assertNotNull(player)
        assertEquals("TestPlayer", player?.name)
    }
    
    @Test
    fun testGetCurrentPlayer() {
        StatisticsManager.createPlayer("TestPlayer")
        
        val currentPlayer = StatisticsManager.getCurrentPlayer()
        assertNotNull(currentPlayer)
        assertEquals("TestPlayer", currentPlayer?.name)
    }
    
    @Test
    fun testSetCurrentPlayer() {
        StatisticsManager.createPlayer("TestPlayer1")
        StatisticsManager.createPlayer("TestPlayer2")
        
        assertTrue(StatisticsManager.setCurrentPlayer("TestPlayer2"))
        val currentPlayer = StatisticsManager.getCurrentPlayer()
        assertEquals("TestPlayer2", currentPlayer?.name)
    }
    
    @Test
    fun testUpdatePlayerStats() {
        StatisticsManager.createPlayer("TestPlayer")
        
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
        
        StatisticsManager.updatePlayerStats(session)
        
        val player = StatisticsManager.getCurrentPlayer()
        assertNotNull(player)
        assertEquals(1, player?.totalGames)
        assertEquals(1, player?.wonGames)
        assertEquals(120, player?.bestTime)
    }
    
    @Test
    fun testGetPlayerRecords() {
        StatisticsManager.createPlayer("TestPlayer")
        
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
        
        StatisticsManager.updatePlayerStats(session)
        
        val records = StatisticsManager.getPlayerRecords("TestPlayer")
        assertEquals(1, records["totalGames"])
        assertEquals(1, records["wonGames"])
        assertEquals(120, records["bestTime"])
    }
    
    @Test
    fun testGetPlayerGameHistory() {
        StatisticsManager.createPlayer("TestPlayer")
        
        val session1 = GameSession(
            playerName = "TestPlayer",
            difficulty = 4,
            time = 120,
            attempts = 15,
            matchedPairs = 8,
            date = System.currentTimeMillis(),
            won = true,
            rating = 5
        )
        
        val session2 = GameSession(
            playerName = "TestPlayer",
            difficulty = 6,
            time = 200,
            attempts = 25,
            matchedPairs = 18,
            date = System.currentTimeMillis(),
            won = true,
            rating = 4
        )
        
        StatisticsManager.updatePlayerStats(session1)
        StatisticsManager.updatePlayerStats(session2)
        
        val history = StatisticsManager.getPlayerGameHistory("TestPlayer")
        assertEquals(2, history.size)
    }
    
    @Test
    fun testPlayerExists() {
        StatisticsManager.createPlayer("TestPlayer")
        
        assertTrue(StatisticsManager.playerExists("TestPlayer"))
        assertFalse(StatisticsManager.playerExists("NonExistentPlayer"))
    }
    
    @Test
    fun testRemovePlayer() {
        StatisticsManager.createPlayer("TestPlayer1")
        StatisticsManager.createPlayer("TestPlayer2")
        
        assertTrue(StatisticsManager.removePlayer("TestPlayer1"))
        assertFalse(StatisticsManager.playerExists("TestPlayer1"))
        assertTrue(StatisticsManager.playerExists("TestPlayer2"))
    }
    
    @Test
    fun testSaveAndLoadStatistics() {
        StatisticsManager.createPlayer("TestPlayer")
        
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
        
        StatisticsManager.updatePlayerStats(session)
        
        // Сохраняем
        assertTrue(StatisticsManager.saveStatistics())
        
        // Сбрасываем состояние
        StatisticsManager.resetForTesting()
        StatisticsManager.setTestPaths(testDataDir, testStatisticsFile)
        
        // Загружаем
        assertTrue(StatisticsManager.loadStatistics())
        
        val player = StatisticsManager.getCurrentPlayer()
        assertNotNull(player)
        assertEquals("TestPlayer", player?.name)
        assertEquals(1, player?.totalGames)
    }
}

