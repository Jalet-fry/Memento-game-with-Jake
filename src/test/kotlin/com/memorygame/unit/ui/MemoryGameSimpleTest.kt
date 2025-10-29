package com.memorygame.unit.ui

import com.memorygame.ui.MemoryGame
import com.memorygame.ui.MemoryCard
import com.memorygame.logic.*
import com.memorygame.backend.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import io.mockk.*

/**
 * Простые unit тесты для MemoryGame
 * 
 * Только базовые тесты без GUI инициализации
 */
class MemoryGameSimpleTest {

    private lateinit var memoryGame: MemoryGame
    private lateinit var testCards: List<MemoryCard>

    @BeforeEach
    fun setUp() {
        // Создаем MemoryGame без инициализации GUI
        memoryGame = createMemoryGameWithoutGUI()
        
        // Создаем тестовые карточки
        testCards = listOf(
            MemoryCard(1, "/images/card1.jpg"),
            MemoryCard(1, "/images/card1.jpg"),
            MemoryCard(2, "/images/card2.jpg"),
            MemoryCard(2, "/images/card2.jpg")
        )
    }

    private fun createMemoryGameWithoutGUI(): MemoryGame {
        // Создаем MemoryGame через рефлексию, пропуская GUI инициализацию
        val memoryGameClass = MemoryGame::class.java
        val constructor = memoryGameClass.getDeclaredConstructor()
        constructor.isAccessible = true
        
        return constructor.newInstance()
    }

    @Test
    fun `test checkMatch method`() {
        // Act & Assert
        assertDoesNotThrow { memoryGame.checkMatch() }
    }

    @Test
    fun `test resetGame method`() {
        // Act & Assert
        assertDoesNotThrow { memoryGame.resetGame() }
    }

    @Test
    fun `test setFirstCard and getFirstCard methods`() {
        // Arrange
        val card = testCards[0]
        
        // Act
        memoryGame.setFirstCard(card)
        val result = memoryGame.getFirstCard()
        
        // Assert
        assertEquals(card, result)
    }

    @Test
    fun `test setSecondCard and getSecondCard methods`() {
        // Arrange
        val card = testCards[1]
        
        // Act
        memoryGame.setSecondCard(card)
        val result = memoryGame.getSecondCard()
        
        // Assert
        assertEquals(card, result)
    }

    @Test
    fun `test clearSelectedCards method`() {
        // Arrange
        memoryGame.setFirstCard(testCards[0])
        memoryGame.setSecondCard(testCards[1])
        
        // Act
        memoryGame.clearSelectedCards()
        
        // Assert
        assertNull(memoryGame.getFirstCard())
        assertNull(memoryGame.getSecondCard())
    }

    @Test
    fun `test incrementMatchedPairs method`() {
        // Arrange
        val initialPairs = memoryGame.getMatchedPairs()
        
        // Act
        memoryGame.incrementMatchedPairs()
        
        // Assert
        assertEquals(initialPairs + 1, memoryGame.getMatchedPairs())
    }

    @Test
    fun `test incrementAttempts method`() {
        // Arrange
        val initialAttempts = memoryGame.getAttempts()
        
        // Act
        memoryGame.incrementAttempts()
        
        // Assert
        assertEquals(initialAttempts + 1, memoryGame.getAttempts())
    }

    @Test
    fun `test getMatchedPairs method`() {
        // Act
        val pairs = memoryGame.getMatchedPairs()
        
        // Assert
        assertTrue(pairs >= 0)
    }

    @Test
    fun `test getAttempts method`() {
        // Act
        val attempts = memoryGame.getAttempts()
        
        // Assert
        assertTrue(attempts >= 0)
    }

    @Test
    fun `test setGameState method`() {
        // Arrange
        val mockGameState = mockk<GameState>()
        every { mockGameState.name } returns "TestState"
        every { mockGameState.description } returns "Test Description"
        
        // Act
        memoryGame.setGameState(mockGameState)
        
        // Assert
        assertDoesNotThrow { memoryGame.setGameState(mockGameState) }
    }

    @Test
    fun `test resetGameState method`() {
        // Act
        memoryGame.resetGameState()
        
        // Assert
        assertDoesNotThrow { memoryGame.resetGameState() }
    }

    @Test
    fun `test onGameEvent method with CARD_FLIPPED`() {
        // Act
        memoryGame.onGameEvent(GameEvent.CARD_FLIPPED, testCards[0])
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.CARD_FLIPPED, testCards[0]) }
    }

    @Test
    fun `test onGameEvent method with CARDS_MATCHED`() {
        // Act
        memoryGame.onGameEvent(GameEvent.CARDS_MATCHED, 1)
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.CARDS_MATCHED, 1) }
    }

    @Test
    fun `test onGameEvent method with CARDS_MISMATCHED`() {
        // Act
        memoryGame.onGameEvent(GameEvent.CARDS_MISMATCHED, 1)
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.CARDS_MISMATCHED, 1) }
    }

    @Test
    fun `test onGameEvent method with GAME_WON`() {
        // Act
        memoryGame.onGameEvent(GameEvent.GAME_WON, 8)
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.GAME_WON, 8) }
    }

    @Test
    fun `test onGameEvent method with GAME_RESET`() {
        // Act
        memoryGame.onGameEvent(GameEvent.GAME_RESET, null)
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.GAME_RESET, null) }
    }

    @Test
    fun `test onGameEvent method with SETTINGS_CHANGED`() {
        // Act
        memoryGame.onGameEvent(GameEvent.SETTINGS_CHANGED, "theme")
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.SETTINGS_CHANGED, "theme") }
    }

    @Test
    fun `test onGameEvent method with ACHIEVEMENT_UNLOCKED`() {
        // Act
        memoryGame.onGameEvent(GameEvent.ACHIEVEMENT_UNLOCKED, "🏆 Первая игра!")
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.ACHIEVEMENT_UNLOCKED, "🏆 Первая игра!") }
    }

    @Test
    fun `test onGameEvent method with null data`() {
        // Act
        memoryGame.onGameEvent(GameEvent.GAME_RESET, null)
        
        // Assert
        assertDoesNotThrow { memoryGame.onGameEvent(GameEvent.GAME_RESET, null) }
    }

    @Test
    fun `test onGameEvent method with all event types`() {
        // Act & Assert
        val allEvents = GameEvent.values()
        allEvents.forEach { event ->
            assertDoesNotThrow {
                memoryGame.onGameEvent(event, "test_data")
            }
        }
    }

    @Test
    fun `test onGameEvent method with different data types`() {
        // Act & Assert
        val testData = listOf(
            GameEvent.CARD_FLIPPED to testCards[0],
            GameEvent.CARDS_MATCHED to 1,
            GameEvent.CARDS_MISMATCHED to 1,
            GameEvent.GAME_WON to 8,
            GameEvent.GAME_RESET to null,
            GameEvent.SETTINGS_CHANGED to "theme",
            GameEvent.ACHIEVEMENT_UNLOCKED to "🏆 Первая игра!"
        )
        
        testData.forEach { (event, data) ->
            assertDoesNotThrow {
                memoryGame.onGameEvent(event, data)
            }
        }
    }

    @Test
    fun `test onGameEvent method with edge cases`() {
        // Act & Assert
        val edgeCases = listOf(
            GameEvent.CARD_FLIPPED to "",
            GameEvent.CARDS_MATCHED to 0,
            GameEvent.CARDS_MISMATCHED to -1,
            GameEvent.GAME_WON to Int.MAX_VALUE,
            GameEvent.GAME_RESET to null,
            GameEvent.SETTINGS_CHANGED to " ",
            GameEvent.ACHIEVEMENT_UNLOCKED to "🏆"
        )
        
        edgeCases.forEach { (event, data) ->
            assertDoesNotThrow {
                memoryGame.onGameEvent(event, data)
            }
        }
    }
}
