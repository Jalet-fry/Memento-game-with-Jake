package com.memorygame

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import javax.swing.SwingUtilities
import java.awt.EventQueue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemoryGameTest {

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
    fun `test game initialization`() {
        // Проверяем начальное состояние игры
        assertNotNull(memoryGame, "Игра должна быть создана")
        assertNotNull(memoryGame, "Game should be created")
        assertEquals("Игра Мементо", memoryGame.title, "Game title should be correct")
        assertEquals("Игра Мементо", memoryGame.title, "Заголовок окна должен быть корректным")
    }

    @Test
    fun `test format time function`() {
        // Тестируем форматирование времени
        // Используем рефлексию для доступа к приватному методу
        val formatTimeMethod = MemoryGame::class.java.getDeclaredMethod("formatTime", Int::class.java)
        formatTimeMethod.isAccessible = true
        
        assertEquals("00:00", formatTimeMethod.invoke(memoryGame, 0), "0 секунд должно форматироваться как 00:00")
        assertEquals("00:05", formatTimeMethod.invoke(memoryGame, 5), "5 секунд должно форматироваться как 00:05")
        assertEquals("00:59", formatTimeMethod.invoke(memoryGame, 59), "59 секунд должно форматироваться как 00:59")
        assertEquals("01:00", formatTimeMethod.invoke(memoryGame, 60), "60 секунд должно форматироваться как 01:00")
        assertEquals("01:30", formatTimeMethod.invoke(memoryGame, 90), "90 секунд должно форматироваться как 01:30")
        assertEquals("02:15", formatTimeMethod.invoke(memoryGame, 135), "135 секунд должно форматироваться как 02:15")
    }

    @Test
    fun `test game state after reset`() {
        // Проверяем состояние игры после сброса
        SwingUtilities.invokeAndWait {
            // Используем рефлексию для доступа к приватному методу
            val resetGameMethod = MemoryGame::class.java.getDeclaredMethod("resetGame")
            resetGameMethod.isAccessible = true
            resetGameMethod.invoke(memoryGame)
        }
        
        // Проверяем, что игра находится в начальном состоянии
        assertNotNull(memoryGame, "Game should remain valid after reset")
    }

    @Test
    fun `test card matching logic`() {
        // Проверяем логику совпадения карточек через рефлексию
        SwingUtilities.invokeAndWait {
            val cardsField = MemoryGame::class.java.getDeclaredField("cards")
            cardsField.isAccessible = true
            
            @Suppress("UNCHECKED_CAST")
            val cards = cardsField.get(memoryGame) as MutableList<MemoryCard>
            
            // Проверяем, что есть карточки с одинаковыми ID (пары)
            val cardIds = cards.map { it.getCardId() }
            val uniqueIds = cardIds.toSet()
            
            assertEquals(8, uniqueIds.size, "Should have 8 unique card IDs for pairs")
            assertEquals(16, cards.size, "Should have 16 total cards")
            
            // Проверяем, что каждая пара имеет одинаковый ID
            val idCounts = cardIds.groupingBy { it }.eachCount()
            idCounts.values.forEach { count ->
                assertEquals(2, count, "Each card ID should appear exactly twice")
            }
        }
    }

    @Test
    fun `test win condition`() {
        // Проверяем условие победы через DifficultyManager
        SwingUtilities.invokeAndWait {
            val strategy = DifficultyManager.getCurrentStrategy()
            val matchedPairsField = MemoryGame::class.java.getDeclaredField("matchedPairs")
            matchedPairsField.isAccessible = true
            
            // Проверяем начальное состояние
            assertEquals(0, matchedPairsField.getInt(memoryGame), "Initial matched pairs should be 0")
            assertEquals(8, strategy.totalPairs, "Total pairs should be 8 for 4x4 grid")
            
            // Симулируем победу, устанавливая все пары как совпавшие
            matchedPairsField.setInt(memoryGame, strategy.totalPairs)
            
            assertEquals(strategy.totalPairs, matchedPairsField.getInt(memoryGame), "All pairs should be matched for win condition")
        }
    }

    @Test
    fun `test game settings`() {
        // Проверяем настройки игры через SettingsManager
        // Проверяем начальные значения настроек
        assertTrue(SettingsManager.animationsEnabled, "Анимации должны быть включены по умолчанию")
        assertTrue(SettingsManager.soundEnabled, "Звук должен быть включен по умолчанию")
        assertEquals("dark", SettingsManager.currentTheme, "Тема должна быть темной по умолчанию")
        assertEquals(4, SettingsManager.difficulty, "Сложность должна быть 4x4 по умолчанию")
    }

    @Test
    fun `test achievement system`() {
        // Проверяем систему достижений через SettingsManager
        // Проверяем начальные значения статистики
        assertEquals(0, SettingsManager.gamesPlayed, "Количество сыгранных игр должно быть 0")
        assertEquals(Int.MAX_VALUE, SettingsManager.bestTime, "Лучшее время должно быть максимальным значением")
        assertEquals(0, SettingsManager.totalMatches, "Общее количество совпадений должно быть 0")
        assertTrue(SettingsManager.achievements.isEmpty(), "Список достижений должен быть пустым")
    }

    @Test
    fun `test image paths configuration`() {
        // Проверяем конфигурацию путей к изображениям
        val imagePathsField = MemoryGame::class.java.getDeclaredField("imagePaths")
        imagePathsField.isAccessible = true
        
        @Suppress("UNCHECKED_CAST")
        val imagePaths = imagePathsField.get(memoryGame) as List<String>
        
        assertEquals(8, imagePaths.size, "Должно быть 8 путей к изображениям")
        assertTrue(imagePaths.all { it.startsWith("/images/card") }, "Все пути должны начинаться с /images/card")
        assertTrue(imagePaths.all { it.endsWith(".jpg") }, "Все пути должны заканчиваться на .jpg")
    }

    @Test
    fun `test animation paths configuration`() {
        // Проверяем конфигурацию путей к анимациям
        val animationPathsField = MemoryGame::class.java.getDeclaredField("animationPaths")
        animationPathsField.isAccessible = true
        
        @Suppress("UNCHECKED_CAST")
        val animationPaths = animationPathsField.get(memoryGame) as List<String>
        
        assertEquals(20, animationPaths.size, "Должно быть 20 путей к анимациям")
        assertTrue(animationPaths.all { it.startsWith("/animations/") }, "Все пути должны начинаться с /animations/")
        assertTrue(animationPaths.all { it.endsWith(".gif") }, "Все пути должны заканчиваться на .gif")
    }

    @Test
    fun `test special animations configuration`() {
        // Проверяем конфигурацию специальных анимаций
        val specialAnimationsField = MemoryGame::class.java.getDeclaredField("specialAnimations")
        specialAnimationsField.isAccessible = true
        
        @Suppress("UNCHECKED_CAST")
        val specialAnimations = specialAnimationsField.get(memoryGame) as Map<String, String>
        
        assertEquals(4, specialAnimations.size, "Должно быть 4 специальные анимации")
        assertTrue(specialAnimations.containsKey("match"), "Должна быть анимация для совпадения")
        assertTrue(specialAnimations.containsKey("win"), "Должна быть анимация для победы")
        assertTrue(specialAnimations.containsKey("miss"), "Должна быть анимация для промаха")
        assertTrue(specialAnimations.containsKey("start"), "Должна быть анимация для старта")
    }

    @Test
    fun `test grid size configuration`() {
        // Проверяем конфигурацию размера сетки через DifficultyManager
        val strategy = DifficultyManager.getCurrentStrategy()
        
        assertEquals(4, strategy.gridSize, "Размер сетки должен быть 4x4")
        assertEquals(8, strategy.totalPairs, "Количество пар должно быть 8 для сетки 4x4")
        assertEquals((strategy.gridSize * strategy.gridSize) / 2, strategy.totalPairs, "Количество пар должно соответствовать размеру сетки")
    }

    @Test
    fun `test game window properties`() {
        // Проверяем свойства окна игры
        assertEquals("Игра Мементо", memoryGame.title, "Window title should be correct")
        assertTrue(memoryGame.width > 0, "Window width should be greater than 0")
        assertTrue(memoryGame.height > 0, "Window height should be greater than 0")
        assertEquals(1200, memoryGame.width, "Window width should be 1200")
        assertEquals(900, memoryGame.height, "Window height should be 900")
    }

    @Test
    fun `test game statistics tracking`() {
        // Проверяем отслеживание статистики через SettingsManager
        // Проверяем начальные значения
        assertEquals(0, SettingsManager.gamesPlayed, "Количество игр должно быть 0")
        assertEquals(Int.MAX_VALUE, SettingsManager.bestTime, "Лучшее время должно быть максимальным")
        assertEquals(0, SettingsManager.totalMatches, "Общее количество совпадений должно быть 0")
        
        // Проверяем, что достижения пусты
        assertTrue(SettingsManager.achievements.isEmpty(), "Список достижений должен быть пустым")
    }

    @Test
    fun `test game state variables`() {
        // Проверяем переменные состояния игры
        val attemptsField = MemoryGame::class.java.getDeclaredField("attempts")
        attemptsField.isAccessible = true
        
        val elapsedSecondsField = MemoryGame::class.java.getDeclaredField("elapsedSeconds")
        elapsedSecondsField.isAccessible = true
        
        val matchedPairsField = MemoryGame::class.java.getDeclaredField("matchedPairs")
        matchedPairsField.isAccessible = true
        
        val isBusyField = MemoryGame::class.java.getDeclaredField("isBusy")
        isBusyField.isAccessible = true
        
        // Проверяем начальные значения
        assertEquals(0, attemptsField.getInt(memoryGame), "Количество попыток должно быть 0")
        assertEquals(0, elapsedSecondsField.getInt(memoryGame), "Прошедшее время должно быть 0")
        assertEquals(0, matchedPairsField.getInt(memoryGame), "Количество совпадений должно быть 0")
        assertFalse(isBusyField.getBoolean(memoryGame), "Игра не должна быть занята")
    }

    @Test
    fun `test game cards list`() {
        // Проверяем список карточек
        val cardsField = MemoryGame::class.java.getDeclaredField("cards")
        cardsField.isAccessible = true
        
        @Suppress("UNCHECKED_CAST")
        val cards = cardsField.get(memoryGame) as MutableList<MemoryCard>
        
        assertEquals(16, cards.size, "Должно быть создано 16 карточек для сетки 4x4")
        
        // Проверяем, что все карточки в начальном состоянии
        cards.forEach { card ->
            assertFalse(card.isFlipped, "Все карточки должны быть закрыты при инициализации")
            assertFalse(card.isMatched, "Все карточки не должны быть совпавшими при инициализации")
            assertTrue(card.isEnabled, "Все карточки должны быть активны при инициализации")
        }
    }

    @Test
    fun `test game timer job`() {
        // Проверяем работу таймера
        val timerJobField = MemoryGame::class.java.getDeclaredField("timerJob")
        timerJobField.isAccessible = true
        
        val timerJob = timerJobField.get(memoryGame)
        assertNotNull(timerJob, "Таймер должен быть запущен")
    }

    @Test
    fun `test game difficulty levels`() {
        // Проверяем уровни сложности через SettingsManager
        val difficulty = SettingsManager.difficulty
        assertTrue(difficulty in listOf(4, 6, 8), "Сложность должна быть 4, 6 или 8")
        assertEquals(4, difficulty, "Сложность по умолчанию должна быть 4x4")
    }

    @Test
    fun `test game theme settings`() {
        // Проверяем настройки темы через SettingsManager
        val currentTheme = SettingsManager.currentTheme
        assertTrue(currentTheme in listOf("dark", "light"), "Тема должна быть dark или light")
        assertEquals("dark", currentTheme, "Тема по умолчанию должна быть темной")
    }

    @Test
    fun `test game sound settings`() {
        // Проверяем настройки звука через SettingsManager
        val soundEnabled = SettingsManager.soundEnabled
        assertTrue(soundEnabled, "Звук должен быть включен по умолчанию")
    }

    @Test
    fun `test game animation settings`() {
        // Проверяем настройки анимации через SettingsManager
        val animationsEnabled = SettingsManager.animationsEnabled
        assertTrue(animationsEnabled, "Анимации должны быть включены по умолчанию")
    }

    @Test
    fun `test game grid size calculation`() {
        // Проверяем расчет размера сетки через DifficultyManager
        val strategy = DifficultyManager.getCurrentStrategy()
        
        assertEquals(4, strategy.gridSize, "Размер сетки должен быть 4x4")
        assertEquals(8, strategy.totalPairs, "Количество пар должно быть 8 для сетки 4x4")
        assertEquals((strategy.gridSize * strategy.gridSize) / 2, strategy.totalPairs, "Количество пар должно соответствовать размеру сетки")
    }

    @Test
    fun `test game resource paths validation`() {
        // Проверяем валидность путей к ресурсам
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
        
        assertEquals(8, imagePaths.size, "Должно быть 8 путей к изображениям")
        assertEquals(20, animationPaths.size, "Должно быть 20 путей к анимациям")
    }

    @Test
    fun `test game special animations configuration`() {
        // Проверяем конфигурацию специальных анимаций
        val specialAnimationsField = MemoryGame::class.java.getDeclaredField("specialAnimations")
        specialAnimationsField.isAccessible = true
        
        @Suppress("UNCHECKED_CAST")
        val specialAnimations = specialAnimationsField.get(memoryGame) as Map<String, String>
        
        assertEquals(4, specialAnimations.size, "Должно быть 4 специальные анимации")
        assertTrue(specialAnimations.containsKey("match"), "Должна быть анимация для совпадения")
        assertTrue(specialAnimations.containsKey("win"), "Должна быть анимация для победы")
        assertTrue(specialAnimations.containsKey("miss"), "Должна быть анимация для промаха")
        assertTrue(specialAnimations.containsKey("start"), "Должна быть анимация для старта")
        
        // Проверяем формат путей
        specialAnimations.values.forEach { path ->
            assertTrue(path.startsWith("/animations/"), "Путь к специальной анимации должен начинаться с /animations/")
            assertTrue(path.endsWith(".gif"), "Путь к специальной анимации должен заканчиваться на .gif")
        }
    }

    @Test
    fun `test game window title and properties`() {
        // Проверяем заголовок и свойства окна
        assertEquals("Игра Мементо", memoryGame.title, "Заголовок окна должен быть корректным")
        assertTrue(memoryGame.width > 0, "Ширина окна должна быть больше 0")
        assertTrue(memoryGame.height > 0, "Высота окна должна быть больше 0")
        assertTrue(memoryGame.location.x >= 0, "X координата окна должна быть неотрицательной")
        assertTrue(memoryGame.location.y >= 0, "Y координата окна должна быть неотрицательной")
    }
}
