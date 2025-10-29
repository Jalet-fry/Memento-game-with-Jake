package com.memorygame.unit.backend

import com.memorygame.backend.*
import com.memorygame.ui.MemoryCard
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для GameEventManager и GameObserver
 * 
 * Тестирует систему событий игры и наблюдателей.
 */
class GameEventManagerTest {

    private lateinit var testObserver: TestGameObserver
    private lateinit var testObserver2: TestGameObserver

    @BeforeEach
    fun setUp() {
        GameEventManager.clearObservers()
        testObserver = TestGameObserver()
        testObserver2 = TestGameObserver()
    }

    @Test
    fun `test subscribe observer`() {
        assertEquals(0, GameEventManager.getObserverCount())
        
        GameEventManager.subscribe(testObserver)
        assertEquals(1, GameEventManager.getObserverCount())
        
        GameEventManager.subscribe(testObserver2)
        assertEquals(2, GameEventManager.getObserverCount())
    }

    @Test
    fun `test subscribe same observer twice`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver)
        
        assertEquals(1, GameEventManager.getObserverCount())
    }

    @Test
    fun `test unsubscribe observer`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        assertEquals(2, GameEventManager.getObserverCount())
        
        GameEventManager.unsubscribe(testObserver)
        assertEquals(1, GameEventManager.getObserverCount())
        
        GameEventManager.unsubscribe(testObserver2)
        assertEquals(0, GameEventManager.getObserverCount())
    }

    @Test
    fun `test unsubscribe non existent observer`() {
        GameEventManager.subscribe(testObserver)
        assertEquals(1, GameEventManager.getObserverCount())
        
        GameEventManager.unsubscribe(testObserver2)
        assertEquals(1, GameEventManager.getObserverCount())
    }

    @Test
    fun `test notify observers card flipped`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        val card = MemoryCard(1, "/images/card1.jpg")
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
        
        assertEquals(1, testObserver.cardFlippedCount)
        assertEquals(1, testObserver2.cardFlippedCount)
        assertEquals(card, testObserver.lastCardFlipped)
        assertEquals(card, testObserver2.lastCardFlipped)
    }

    @Test
    fun `test notify observers cards matched`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, 5)
        
        assertEquals(1, testObserver.cardsMatchedCount)
        assertEquals(1, testObserver2.cardsMatchedCount)
        assertEquals(5, testObserver.lastMatchedPairs)
        assertEquals(5, testObserver2.lastMatchedPairs)
    }

    @Test
    fun `test notify observers cards mismatched`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, 3)
        
        assertEquals(1, testObserver.cardsMismatchedCount)
        assertEquals(1, testObserver2.cardsMismatchedCount)
        assertEquals(3, testObserver.lastAttempts)
        assertEquals(3, testObserver2.lastAttempts)
    }

    @Test
    fun `test notify observers game won`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.GAME_WON, 8)
        
        assertEquals(1, testObserver.gameWonCount)
        assertEquals(1, testObserver2.gameWonCount)
        assertEquals(8, testObserver.lastMatchedPairs)
        assertEquals(8, testObserver2.lastMatchedPairs)
    }

    @Test
    fun `test notify observers game reset`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
        
        assertEquals(1, testObserver.gameResetCount)
        assertEquals(1, testObserver2.gameResetCount)
    }

    @Test
    fun `test notify observers settings changed`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "theme")
        
        assertEquals(1, testObserver.settingsChangedCount)
        assertEquals(1, testObserver2.settingsChangedCount)
        assertEquals("theme", testObserver.lastSettingsData)
        assertEquals("theme", testObserver2.lastSettingsData)
    }

    @Test
    fun `test notify observers achievement unlocked`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        GameEventManager.notifyObservers(GameEvent.ACHIEVEMENT_UNLOCKED, "🏆 Первая игра!")
        
        assertEquals(1, testObserver.achievementUnlockedCount)
        assertEquals(1, testObserver2.achievementUnlockedCount)
        assertEquals("🏆 Первая игра!", testObserver.lastAchievement)
        assertEquals("🏆 Первая игра!", testObserver2.lastAchievement)
    }

    @Test
    fun `test notify observers with null data`() {
        GameEventManager.subscribe(testObserver)
        
        GameEventManager.notifyObservers(GameEvent.GAME_RESET, null)
        
        assertEquals(1, testObserver.gameResetCount)
    }

    @Test
    fun `test notify observers with no observers`() {
        // Не должно вызывать исключений
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, null)
    }

    @Test
    fun `test clear observers`() {
        GameEventManager.subscribe(testObserver)
        GameEventManager.subscribe(testObserver2)
        
        assertEquals(2, GameEventManager.getObserverCount())
        
        GameEventManager.clearObservers()
        assertEquals(0, GameEventManager.getObserverCount())
        
        // Уведомления не должны доходить до наблюдателей
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
        assertEquals(0, testObserver.gameResetCount)
        assertEquals(0, testObserver2.gameResetCount)
    }

    @Test
    fun `test observer error handling`() {
        val errorObserver = ErrorGameObserver()
        GameEventManager.subscribe(errorObserver)
        GameEventManager.subscribe(testObserver)
        
        // Уведомление должно дойти до testObserver, несмотря на ошибку в errorObserver
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
        
        assertEquals(1, testObserver.gameResetCount)
    }

    @Test
    fun `test UIEventObserver`() {
        val uiObserver = TestUIEventObserver()
        GameEventManager.subscribe(uiObserver)
        
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, MemoryCard(1, "/images/card1.jpg"))
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, 5)
        GameEventManager.notifyObservers(GameEvent.GAME_WON, 8)
        
        assertEquals(1, uiObserver.cardFlippedCount)
        assertEquals(1, uiObserver.cardsMatchedCount)
        assertEquals(1, uiObserver.gameWonCount)
    }

    @Test
    fun `test UIEventObserver all event types`() {
        val uiObserver = ComprehensiveUIEventObserver()
        GameEventManager.subscribe(uiObserver)
        
        // Тестируем все типы событий
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, MemoryCard(1, "/images/card1.jpg"))
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, 5)
        GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, 3)
        GameEventManager.notifyObservers(GameEvent.GAME_WON, 8)
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
        GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "theme")
        GameEventManager.notifyObservers(GameEvent.ACHIEVEMENT_UNLOCKED, "🏆 Первая игра!")
        
        assertEquals(1, uiObserver.cardFlippedCount)
        assertEquals(1, uiObserver.cardsMatchedCount)
        assertEquals(1, uiObserver.cardsMismatchedCount)
        assertEquals(1, uiObserver.gameWonCount)
        assertEquals(1, uiObserver.gameResetCount)
        assertEquals(1, uiObserver.settingsChangedCount)
        assertEquals(1, uiObserver.achievementUnlockedCount)
    }

    @Test
    fun `test UIEventObserver inheritance`() {
        val uiObserver = TestUIEventObserver()
        
        // Проверяем наследование
        assertTrue(uiObserver is GameObserver)
        assertTrue(uiObserver is UIEventObserver)
    }

    @Test
    fun `test UIEventObserver polymorphism`() {
        val observers = listOf(
            TestUIEventObserver(),
            ComprehensiveUIEventObserver()
        )
        
        observers.forEach { observer ->
            assertTrue(observer is GameObserver)
            assertTrue(observer is UIEventObserver)
        }
    }

    @Test
    fun `test UIEventObserver with null data`() {
        val uiObserver = ComprehensiveUIEventObserver()
        GameEventManager.subscribe(uiObserver)
        
        // Тестируем с null данными
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, null)
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, null)
        GameEventManager.notifyObservers(GameEvent.GAME_RESET, null)
        
        assertEquals(1, uiObserver.cardFlippedCount)
        assertEquals(1, uiObserver.cardsMatchedCount)
        assertEquals(1, uiObserver.gameResetCount)
    }

    @Test
    fun `test UIEventObserver multiple instances`() {
        val uiObserver1 = TestUIEventObserver()
        val uiObserver2 = TestUIEventObserver()
        
        GameEventManager.subscribe(uiObserver1)
        GameEventManager.subscribe(uiObserver2)
        
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, MemoryCard(1, "/images/card1.jpg"))
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, 5)
        
        assertEquals(1, uiObserver1.cardFlippedCount)
        assertEquals(1, uiObserver1.cardsMatchedCount)
        assertEquals(1, uiObserver2.cardFlippedCount)
        assertEquals(1, uiObserver2.cardsMatchedCount)
    }

    @Test
    fun `test UIEventObserver event routing`() {
        val uiObserver = ComprehensiveUIEventObserver()
        GameEventManager.subscribe(uiObserver)
        
        // Тестируем маршрутизацию событий
        val card = MemoryCard(1, "/images/card1.jpg")
        
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
        assertTrue(uiObserver.lastCardFlippedData === card)
        
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, 5)
        assertEquals(5, uiObserver.lastCardsMatchedData)
        
        GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "difficulty")
        assertEquals("difficulty", uiObserver.lastSettingsChangedData)
    }

    @Test
    fun `test UIEventObserver abstract methods`() {
        // Создаем анонимный класс для тестирования абстрактных методов
        val uiObserver = object : UIEventObserver() {
            var cardFlippedCalled = false
            var cardsMatchedCalled = false
            var cardsMismatchedCalled = false
            var gameWonCalled = false
            var gameResetCalled = false
            var settingsChangedCalled = false
            var achievementUnlockedCalled = false
            
            override fun onCardFlipped(data: Any?) {
                cardFlippedCalled = true
            }
            
            override fun onCardsMatched(data: Any?) {
                cardsMatchedCalled = true
            }
            
            override fun onCardsMismatched(data: Any?) {
                cardsMismatchedCalled = true
            }
            
            override fun onGameWon(data: Any?) {
                gameWonCalled = true
            }
            
            override fun onGameReset(data: Any?) {
                gameResetCalled = true
            }
            
            override fun onSettingsChanged(data: Any?) {
                settingsChangedCalled = true
            }
            
            override fun onAchievementUnlocked(data: Any?) {
                achievementUnlockedCalled = true
            }
        }
        
        GameEventManager.subscribe(uiObserver)
        
        // Тестируем все события
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, null)
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, null)
        GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, null)
        GameEventManager.notifyObservers(GameEvent.GAME_WON, null)
        GameEventManager.notifyObservers(GameEvent.GAME_RESET, null)
        GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, null)
        GameEventManager.notifyObservers(GameEvent.ACHIEVEMENT_UNLOCKED, null)
        
        assertTrue(uiObserver.cardFlippedCalled)
        assertTrue(uiObserver.cardsMatchedCalled)
        assertTrue(uiObserver.cardsMismatchedCalled)
        assertTrue(uiObserver.gameWonCalled)
        assertTrue(uiObserver.gameResetCalled)
        assertTrue(uiObserver.settingsChangedCalled)
        assertTrue(uiObserver.achievementUnlockedCalled)
    }

    @Test
    fun `test UIEventObserver default implementation`() {
        // Создаем наблюдатель без переопределения методов
        val uiObserver = object : UIEventObserver() {}
        
        GameEventManager.subscribe(uiObserver)
        
        // Не должно вызывать исключений
        assertDoesNotThrow {
            GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, null)
            GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, null)
            GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, null)
            GameEventManager.notifyObservers(GameEvent.GAME_WON, null)
            GameEventManager.notifyObservers(GameEvent.GAME_RESET, null)
            GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, null)
            GameEventManager.notifyObservers(GameEvent.ACHIEVEMENT_UNLOCKED, null)
        }
    }

    @Test
    fun `test game event enum values`() {
        val events = GameEvent.values()
        
        assertEquals(7, events.size)
        assertTrue(events.contains(GameEvent.CARD_FLIPPED))
        assertTrue(events.contains(GameEvent.CARDS_MATCHED))
        assertTrue(events.contains(GameEvent.CARDS_MISMATCHED))
        assertTrue(events.contains(GameEvent.GAME_WON))
        assertTrue(events.contains(GameEvent.GAME_RESET))
        assertTrue(events.contains(GameEvent.SETTINGS_CHANGED))
        assertTrue(events.contains(GameEvent.ACHIEVEMENT_UNLOCKED))
    }
}

/**
 * Тестовый наблюдатель для проверки событий
 */
class TestGameObserver : GameObserver {
    var cardFlippedCount = 0
    var cardsMatchedCount = 0
    var cardsMismatchedCount = 0
    var gameWonCount = 0
    var gameResetCount = 0
    var settingsChangedCount = 0
    var achievementUnlockedCount = 0
    
    var lastCardFlipped: MemoryCard? = null
    var lastMatchedPairs: Int? = null
    var lastAttempts: Int? = null
    var lastSettingsData: Any? = null
    var lastAchievement: Any? = null
    
    override fun onGameEvent(event: GameEvent, data: Any?) {
        when (event) {
            GameEvent.CARD_FLIPPED -> {
                cardFlippedCount++
                lastCardFlipped = data as? MemoryCard
            }
            GameEvent.CARDS_MATCHED -> {
                cardsMatchedCount++
                lastMatchedPairs = data as? Int
            }
            GameEvent.CARDS_MISMATCHED -> {
                cardsMismatchedCount++
                lastAttempts = data as? Int
            }
            GameEvent.GAME_WON -> {
                gameWonCount++
                lastMatchedPairs = data as? Int
            }
            GameEvent.GAME_RESET -> {
                gameResetCount++
            }
            GameEvent.SETTINGS_CHANGED -> {
                settingsChangedCount++
                lastSettingsData = data
            }
            GameEvent.ACHIEVEMENT_UNLOCKED -> {
                achievementUnlockedCount++
                lastAchievement = data
            }
        }
    }
}

/**
 * Тестовый наблюдатель, который выбрасывает исключения
 */
class ErrorGameObserver : GameObserver {
    override fun onGameEvent(event: GameEvent, data: Any?) {
        throw RuntimeException("Test error")
    }
}

/**
 * Тестовый UI наблюдатель
 */
class TestUIEventObserver : UIEventObserver() {
    var cardFlippedCount = 0
    var cardsMatchedCount = 0
    var gameWonCount = 0
    
    override fun onCardFlipped(data: Any?) {
        cardFlippedCount++
    }
    
    override fun onCardsMatched(data: Any?) {
        cardsMatchedCount++
    }
    
    override fun onGameWon(data: Any?) {
        gameWonCount++
    }
}

/**
 * Комплексный тестовый UI наблюдатель для всех событий
 */
class ComprehensiveUIEventObserver : UIEventObserver() {
    var cardFlippedCount = 0
    var cardsMatchedCount = 0
    var cardsMismatchedCount = 0
    var gameWonCount = 0
    var gameResetCount = 0
    var settingsChangedCount = 0
    var achievementUnlockedCount = 0
    
    var lastCardFlippedData: Any? = null
    var lastCardsMatchedData: Any? = null
    var lastCardsMismatchedData: Any? = null
    var lastGameWonData: Any? = null
    var lastGameResetData: Any? = null
    var lastSettingsChangedData: Any? = null
    var lastAchievementUnlockedData: Any? = null
    
    override fun onCardFlipped(data: Any?) {
        cardFlippedCount++
        lastCardFlippedData = data
    }
    
    override fun onCardsMatched(data: Any?) {
        cardsMatchedCount++
        lastCardsMatchedData = data
    }
    
    override fun onCardsMismatched(data: Any?) {
        cardsMismatchedCount++
        lastCardsMismatchedData = data
    }
    
    override fun onGameWon(data: Any?) {
        gameWonCount++
        lastGameWonData = data
    }
    
    override fun onGameReset(data: Any?) {
        gameResetCount++
        lastGameResetData = data
    }
    
    override fun onSettingsChanged(data: Any?) {
        settingsChangedCount++
        lastSettingsChangedData = data
    }
    
    override fun onAchievementUnlocked(data: Any?) {
        achievementUnlockedCount++
        lastAchievementUnlockedData = data
    }
}
