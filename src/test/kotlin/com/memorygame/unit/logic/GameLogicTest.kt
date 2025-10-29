package com.memorygame.unit.logic

import com.memorygame.logic.GameLogic
import com.memorygame.backend.SettingsManager
import com.memorygame.backend.DifficultyManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для GameLogic
 * 
 * Тестирует бизнес-логику игры отдельно от UI.
 */
class GameLogicTest {

    private lateinit var gameLogic: GameLogic

    @BeforeEach
    fun setUp() {
        gameLogic = GameLogic()
        SettingsManager.resetToDefaults()
        SettingsManager.resetStats()
    }

    @Test
    fun `test initial state`() {
        // Проверяем начальное состояние
        assertEquals(0, gameLogic.matchedPairs)
        assertEquals(0, gameLogic.attempts)
        assertEquals(0, gameLogic.elapsedSeconds)
        assertFalse(gameLogic.isGameCompleted())
    }

    @Test
    fun `test checkMatch with matching cards`() {
        // Тестируем совпадающие карточки
        val result = gameLogic.checkMatch(1, 1)
        
        assertTrue(result, "Совпадающие карточки должны возвращать true")
        assertEquals(1, gameLogic.matchedPairs, "Количество совпадений должно увеличиться")
        assertEquals(1, gameLogic.attempts, "Количество попыток должно увеличиться")
    }

    @Test
    fun `test checkMatch with non-matching cards`() {
        // Тестируем несовпадающие карточки
        val result = gameLogic.checkMatch(1, 2)
        
        assertFalse(result, "Несовпадающие карточки должны возвращать false")
        assertEquals(0, gameLogic.matchedPairs, "Количество совпадений не должно измениться")
        assertEquals(1, gameLogic.attempts, "Количество попыток должно увеличиться")
    }

    @Test
    fun `test checkMatch multiple attempts`() {
        // Тестируем несколько попыток
        gameLogic.checkMatch(1, 2) // Не совпали
        gameLogic.checkMatch(1, 1) // Совпали
        gameLogic.checkMatch(2, 3) // Не совпали
        gameLogic.checkMatch(2, 2) // Совпали
        
        assertEquals(2, gameLogic.matchedPairs, "Должно быть 2 совпадения")
        assertEquals(4, gameLogic.attempts, "Должно быть 4 попытки")
    }

    @Test
    fun `test checkWin with easy difficulty`() {
        // Тестируем победу на легкой сложности
        DifficultyManager.setStrategy(4)
        val strategy = DifficultyManager.getCurrentStrategy()
        
        // Совпадаем все пары
        repeat(strategy.totalPairs) {
            gameLogic.checkMatch(1, 1)
        }
        
        assertTrue(gameLogic.checkWin(), "Игра должна быть выиграна")
        assertTrue(gameLogic.isGameCompleted(), "Игра должна быть завершена")
    }

    @Test
    fun `test checkWin with medium difficulty`() {
        // Тестируем победу на средней сложности
        DifficultyManager.setStrategy(6)
        val strategy = DifficultyManager.getCurrentStrategy()
        
        // Совпадаем все пары
        repeat(strategy.totalPairs) {
            gameLogic.checkMatch(1, 1)
        }
        
        assertTrue(gameLogic.checkWin(), "Игра должна быть выиграна")
        assertTrue(gameLogic.isGameCompleted(), "Игра должна быть завершена")
    }

    @Test
    fun `test checkWin with hard difficulty`() {
        // Тестируем победу на сложной сложности
        DifficultyManager.setStrategy(8)
        val strategy = DifficultyManager.getCurrentStrategy()
        
        // Совпадаем все пары
        repeat(strategy.totalPairs) {
            gameLogic.checkMatch(1, 1)
        }
        
        assertTrue(gameLogic.checkWin(), "Игра должна быть выиграна")
        assertTrue(gameLogic.isGameCompleted(), "Игра должна быть завершена")
    }

    @Test
    fun `test checkWin not completed`() {
        // Тестируем неполную игру
        DifficultyManager.setStrategy(4)
        
        // Совпадаем только половину пар
        repeat(4) {
            gameLogic.checkMatch(1, 1)
        }
        
        assertFalse(gameLogic.checkWin(), "Игра не должна быть выиграна")
        assertFalse(gameLogic.isGameCompleted(), "Игра не должна быть завершена")
    }

    @Test
    fun `test time management`() {
        // Тестируем управление временем
        gameLogic.updateTime(120)
        assertEquals(120, gameLogic.elapsedSeconds)
        assertEquals("02:00", gameLogic.getFormattedTime())
        
        gameLogic.incrementTime()
        assertEquals(121, gameLogic.elapsedSeconds)
        assertEquals("02:01", gameLogic.getFormattedTime())
    }

    @Test
    fun `test formatTime various values`() {
        // Тестируем форматирование времени
        assertEquals("00:00", gameLogic.formatTime(0))
        assertEquals("00:30", gameLogic.formatTime(30))
        assertEquals("01:00", gameLogic.formatTime(60))
        assertEquals("01:30", gameLogic.formatTime(90))
        assertEquals("02:15", gameLogic.formatTime(135))
        assertEquals("10:45", gameLogic.formatTime(645))
    }

    @Test
    fun `test resetGame`() {
        // Настраиваем игру
        gameLogic.checkMatch(1, 1)
        gameLogic.checkMatch(2, 2)
        gameLogic.updateTime(120)
        
        // Сбрасываем игру
        gameLogic.resetGame()
        
        assertEquals(0, gameLogic.matchedPairs)
        assertEquals(0, gameLogic.attempts)
        assertEquals(0, gameLogic.elapsedSeconds)
        assertFalse(gameLogic.isGameCompleted())
    }

    @Test
    fun `test canMakeMove`() {
        // Тестируем проверку возможности хода
        assertTrue(gameLogic.canMakeMove(false), "Можно делать ход когда не занят")
        assertFalse(gameLogic.canMakeMove(true), "Нельзя делать ход когда занят")
    }

    @Test
    fun `test getGameRating`() {
        // Тестируем получение рейтинга игры
        DifficultyManager.setStrategy(4)
        
        // Быстрая игра с малым количеством попыток
        gameLogic.updateTime(30)
        repeat(8) { gameLogic.checkMatch(1, 1) }
        
        val rating = gameLogic.getGameRating()
        assertTrue(rating >= 1 && rating <= 5, "Рейтинг должен быть от 1 до 5")
    }

    @Test
    fun `test getGameStateInfo`() {
        // Тестируем получение информации о состоянии игры
        gameLogic.checkMatch(1, 1)
        gameLogic.updateTime(65)
        
        val info = gameLogic.getGameStateInfo()
        
        assertTrue(info.contains("Попытки: 1"))
        assertTrue(info.contains("Совпадения: 1/8"))
        assertTrue(info.contains("Время: 01:05"))
    }

    @Test
    fun `test edge cases`() {
        // Тестируем граничные случаи
        
        // Отрицательные ID - одинаковые должны совпадать
        assertTrue(gameLogic.checkMatch(-1, -1), "Одинаковые отрицательные ID должны совпадать")
        assertFalse(gameLogic.checkMatch(-1, 1), "Разные ID не должны совпадать")
        
        // Большие числа - одинаковые должны совпадать
        assertTrue(gameLogic.checkMatch(Int.MAX_VALUE, Int.MAX_VALUE), "Одинаковые большие числа должны совпадать")
        assertFalse(gameLogic.checkMatch(Int.MAX_VALUE, 1), "Разные числа не должны совпадать")
        
        // Нулевые значения
        assertTrue(gameLogic.checkMatch(0, 0), "Нулевые значения должны совпадать")
        assertFalse(gameLogic.checkMatch(0, 1), "Ноль и единица не должны совпадать")
    }

    @Test
    fun `test multiple resets`() {
        // Тестируем множественные сбросы
        repeat(3) {
            gameLogic.checkMatch(1, 1)
            gameLogic.updateTime(60)
            gameLogic.resetGame()
        }
        
        assertEquals(0, gameLogic.matchedPairs)
        assertEquals(0, gameLogic.attempts)
        assertEquals(0, gameLogic.elapsedSeconds)
    }

    @Test
    fun `test time edge cases`() {
        // Тестируем граничные случаи времени
        gameLogic.updateTime(0)
        assertEquals("00:00", gameLogic.getFormattedTime())
        
        gameLogic.updateTime(59)
        assertEquals("00:59", gameLogic.getFormattedTime())
        
        gameLogic.updateTime(60)
        assertEquals("01:00", gameLogic.getFormattedTime())
        
        gameLogic.updateTime(3599)
        assertEquals("59:59", gameLogic.getFormattedTime())
        
        gameLogic.updateTime(3600)
        assertEquals("60:00", gameLogic.getFormattedTime())
    }
}
