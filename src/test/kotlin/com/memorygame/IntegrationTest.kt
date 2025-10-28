package com.memorygame

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import javax.swing.SwingUtilities
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    companion object {
        private var sharedMemoryGame: MemoryGame? = null
    }

    private val memoryGame: MemoryGame
        get() = sharedMemoryGame ?: run {
            SwingUtilities.invokeAndWait {
                sharedMemoryGame = MemoryGame()
            }
            sharedMemoryGame!!
        }

    @BeforeEach
    fun setUp() {
        // Используем общий экземпляр игры
        if (sharedMemoryGame == null) {
            SwingUtilities.invokeAndWait {
                sharedMemoryGame = MemoryGame()
            }
        }
        
        // Сбрасываем состояние игры перед каждым тестом
        SwingUtilities.invokeAndWait {
            val resetGameMethod = MemoryGame::class.java.getDeclaredMethod("resetGame")
            resetGameMethod.isAccessible = true
            resetGameMethod.invoke(sharedMemoryGame)
        }
    }

    @Test
    fun `test complete game initialization`() {
        // Проверяем полную инициализацию игры
        SwingUtilities.invokeAndWait {
            assertNotNull(memoryGame, "Game should be created")
            
            // Проверяем, что все компоненты инициализированы
            val cardsField = MemoryGame::class.java.getDeclaredField("cards")
            cardsField.isAccessible = true
            
            @Suppress("UNCHECKED_CAST")
            val cards = cardsField.get(memoryGame) as MutableList<MemoryCard>
            
            assertEquals(16, cards.size, "Should create 16 cards for 4x4 grid")
            
            // Проверяем, что все карточки в начальном состоянии
            cards.forEach { card ->
                assertFalse(card.isFlipped, "All cards should be closed at initialization")
                assertFalse(card.isMatched, "All cards should not be matched at initialization")
                assertTrue(card.isEnabled, "All cards should be enabled at initialization")
            }
        }
    }

    @Test
    fun `test game state consistency`() {
        // Проверяем согласованность состояния игры
        SwingUtilities.invokeAndWait {
            val attemptsField = MemoryGame::class.java.getDeclaredField("attempts")
            attemptsField.isAccessible = true
            
            val matchedPairsField = MemoryGame::class.java.getDeclaredField("matchedPairs")
            matchedPairsField.isAccessible = true
            
            val isBusyField = MemoryGame::class.java.getDeclaredField("isBusy")
            isBusyField.isAccessible = true
            
            // Проверяем начальные значения
            assertEquals(0, attemptsField.getInt(memoryGame), "Количество попыток должно быть 0")
            assertEquals(0, matchedPairsField.getInt(memoryGame), "Количество совпадений должно быть 0")
            assertFalse(isBusyField.getBoolean(memoryGame), "Игра не должна быть занята")
        }
    }

    @Test
    fun `test timer integration`() {
        // Проверяем интеграцию таймера
        SwingUtilities.invokeAndWait {
            val elapsedSecondsField = MemoryGame::class.java.getDeclaredField("elapsedSeconds")
            elapsedSecondsField.isAccessible = true
            
            val timerJobField = MemoryGame::class.java.getDeclaredField("timerJob")
            timerJobField.isAccessible = true
            
            // Проверяем начальное состояние таймера
            assertEquals(0, elapsedSecondsField.getInt(memoryGame), "Прошедшее время должно быть 0")
            assertNotNull(timerJobField.get(memoryGame), "Таймер должен быть запущен")
        }
    }

    @Test
    fun `test card creation and distribution`() {
        // Проверяем создание и распределение карточек
        SwingUtilities.invokeAndWait {
            val cardsField = MemoryGame::class.java.getDeclaredField("cards")
            cardsField.isAccessible = true
            
            @Suppress("UNCHECKED_CAST")
            val cards = cardsField.get(memoryGame) as MutableList<MemoryCard>
            
            // Проверяем, что созданы правильные пары
            val cardIds = cards.map { it.getCardId() }
            val uniqueIds = cardIds.toSet()
            
            assertEquals(8, uniqueIds.size, "Должно быть 8 уникальных ID карточек")
            
            // Проверяем, что каждая карточка имеет пару
            uniqueIds.forEach { id ->
                val cardsWithId = cardIds.count { it == id }
                assertEquals(2, cardsWithId, "Каждая карточка должна иметь пару")
            }
        }
    }

    @Test
    fun `test settings integration`() {
        // Проверяем интеграцию настроек через SettingsManager
        SwingUtilities.invokeAndWait {
            // Проверяем корректность настроек через SettingsManager
            assertTrue(SettingsManager.animationsEnabled, "Анимации должны быть включены")
            assertTrue(SettingsManager.soundEnabled, "Звук должен быть включен")
            assertEquals("dark", SettingsManager.currentTheme, "Тема должна быть темной")
            assertEquals(4, SettingsManager.difficulty, "Сложность должна быть 4x4")
        }
    }

    @Test
    fun `test resource paths validation`() {
        // Проверяем валидность путей к ресурсам
        SwingUtilities.invokeAndWait {
            val imagePathsField = MemoryGame::class.java.getDeclaredField("imagePaths")
            imagePathsField.isAccessible = true
            
            val animationPathsField = MemoryGame::class.java.getDeclaredField("animationPaths")
            animationPathsField.isAccessible = true
            
            @Suppress("UNCHECKED_CAST")
            val imagePaths = imagePathsField.get(memoryGame) as List<String>
            
            @Suppress("UNCHECKED_CAST")
            val animationPaths = animationPathsField.get(memoryGame) as List<String>
            
            // Проверяем формат путей к изображениям
            imagePaths.forEach { path ->
                assertTrue(path.startsWith("/images/"), "Путь к изображению должен начинаться с /images/")
                assertTrue(path.endsWith(".jpg"), "Путь к изображению должен заканчиваться на .jpg")
            }
            
            // Проверяем формат путей к анимациям
            animationPaths.forEach { path ->
                assertTrue(path.startsWith("/animations/"), "Путь к анимации должен начинаться с /animations/")
                assertTrue(path.endsWith(".gif"), "Путь к анимации должен заканчиваться на .gif")
            }
        }
    }


    @Test
    fun `test game window integration`() {
        // Проверяем интеграцию окна игры
        SwingUtilities.invokeAndWait {
            assertEquals("Игра Мементо", memoryGame.title, "Title should be correct")
            
            // Проверяем размеры окна
            assertTrue(memoryGame.width > 0, "Window width should be greater than 0")
            assertTrue(memoryGame.height > 0, "Window height should be greater than 0")
            
            // Проверяем, что окно находится в центре экрана
            assertTrue(memoryGame.location.x >= 0, "Window X coordinate should be non-negative")
            assertTrue(memoryGame.location.y >= 0, "Window Y coordinate should be non-negative")
        }
    }

    @Test
    fun `test game reset functionality`() {
        // Проверяем функциональность сброса игры
        SwingUtilities.invokeAndWait {
            // Используем рефлексию для доступа к приватному методу
            val resetGameMethod = MemoryGame::class.java.getDeclaredMethod("resetGame")
            resetGameMethod.isAccessible = true
            resetGameMethod.invoke(memoryGame)
            
            // Проверяем, что состояние сброшено
            val attemptsField = MemoryGame::class.java.getDeclaredField("attempts")
            attemptsField.isAccessible = true
            
            val matchedPairsField = MemoryGame::class.java.getDeclaredField("matchedPairs")
            matchedPairsField.isAccessible = true
            
            val elapsedSecondsField = MemoryGame::class.java.getDeclaredField("elapsedSeconds")
            elapsedSecondsField.isAccessible = true
            
            assertEquals(0, attemptsField.getInt(memoryGame), "Попытки должны быть сброшены")
            assertEquals(0, matchedPairsField.getInt(memoryGame), "Совпадения должны быть сброшены")
            assertEquals(0, elapsedSecondsField.getInt(memoryGame), "Время должно быть сброшено")
        }
    }

    @Test
    fun `test achievement system integration`() {
        // Проверяем интеграцию системы достижений через SettingsManager
        SwingUtilities.invokeAndWait {
            // Проверяем начальное состояние системы достижений
            assertTrue(SettingsManager.achievements.isEmpty(), "Список достижений должен быть пустым")
            assertEquals(0, SettingsManager.gamesPlayed, "Количество игр должно быть 0")
            assertEquals(Int.MAX_VALUE, SettingsManager.bestTime, "Лучшее время должно быть максимальным")
            assertEquals(0, SettingsManager.totalMatches, "Общее количество совпадений должно быть 0")
        }
    }
}
