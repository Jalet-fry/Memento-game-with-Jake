package com.memorygame.unit.backend

import com.memorygame.backend.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для DifficultyStrategy классов
 * 
 * Тестирует все стратегии сложности игры и их функциональность.
 */
class DifficultyStrategyTest {

    @Test
    fun `test EasyDifficultyStrategy properties`() {
        val strategy = EasyDifficultyStrategy()
        
        assertEquals(4, strategy.gridSize)
        assertEquals(8, strategy.totalPairs)
        assertEquals("Легко", strategy.name)
        assertEquals("4x4 сетка, 8 пар карточек", strategy.description)
        assertEquals(0, strategy.timeLimit)
        assertEquals(0, strategy.maxAttempts)
    }

    @Test
    fun `test EasyDifficultyStrategy game won`() {
        val strategy = EasyDifficultyStrategy()
        
        assertFalse(strategy.isGameWon(0))
        assertFalse(strategy.isGameWon(7))
        assertTrue(strategy.isGameWon(8))
        assertTrue(strategy.isGameWon(10))
    }

    @Test
    fun `test EasyDifficultyStrategy rating`() {
        val strategy = EasyDifficultyStrategy()
        
        // Отличные результаты
        assertEquals(5, strategy.getGameRating(30, 8))
        assertEquals(5, strategy.getGameRating(25, 6))
        
        // Хорошие результаты
        assertEquals(4, strategy.getGameRating(60, 12))
        assertEquals(4, strategy.getGameRating(45, 10))
        
        // Средние результаты
        assertEquals(3, strategy.getGameRating(120, 16))
        assertEquals(3, strategy.getGameRating(90, 14))
        
        // Плохие результаты
        assertEquals(2, strategy.getGameRating(180, 20))
        assertEquals(2, strategy.getGameRating(150, 18))
        
        // Очень плохие результаты
        assertEquals(1, strategy.getGameRating(200, 25))
        assertEquals(1, strategy.getGameRating(300, 30))
    }

    @Test
    fun `test MediumDifficultyStrategy properties`() {
        val strategy = MediumDifficultyStrategy()
        
        assertEquals(6, strategy.gridSize)
        assertEquals(18, strategy.totalPairs)
        assertEquals("Средне", strategy.name)
        assertEquals("6x6 сетка, 18 пар карточек", strategy.description)
        assertEquals(0, strategy.timeLimit)
        assertEquals(0, strategy.maxAttempts)
    }

    @Test
    fun `test MediumDifficultyStrategy game won`() {
        val strategy = MediumDifficultyStrategy()
        
        assertFalse(strategy.isGameWon(0))
        assertFalse(strategy.isGameWon(17))
        assertTrue(strategy.isGameWon(18))
        assertTrue(strategy.isGameWon(20))
    }

    @Test
    fun `test MediumDifficultyStrategy rating`() {
        val strategy = MediumDifficultyStrategy()
        
        // Отличные результаты
        assertEquals(5, strategy.getGameRating(60, 18))
        assertEquals(5, strategy.getGameRating(45, 15))
        
        // Хорошие результаты
        assertEquals(4, strategy.getGameRating(120, 25))
        assertEquals(4, strategy.getGameRating(90, 20))
        
        // Средние результаты
        assertEquals(3, strategy.getGameRating(240, 35))
        assertEquals(3, strategy.getGameRating(180, 30))
        
        // Плохие результаты
        assertEquals(2, strategy.getGameRating(360, 45))
        assertEquals(2, strategy.getGameRating(300, 40))
        
        // Очень плохие результаты
        assertEquals(1, strategy.getGameRating(400, 50))
        assertEquals(1, strategy.getGameRating(500, 60))
    }

    @Test
    fun `test HardDifficultyStrategy properties`() {
        val strategy = HardDifficultyStrategy()
        
        assertEquals(8, strategy.gridSize)
        assertEquals(32, strategy.totalPairs)
        assertEquals("Сложно", strategy.name)
        assertEquals("8x8 сетка, 32 пары карточек", strategy.description)
        assertEquals(0, strategy.timeLimit)
        assertEquals(0, strategy.maxAttempts)
    }

    @Test
    fun `test HardDifficultyStrategy game won`() {
        val strategy = HardDifficultyStrategy()
        
        assertFalse(strategy.isGameWon(0))
        assertFalse(strategy.isGameWon(31))
        assertTrue(strategy.isGameWon(32))
        assertTrue(strategy.isGameWon(35))
    }

    @Test
    fun `test HardDifficultyStrategy rating`() {
        val strategy = HardDifficultyStrategy()
        
        // Отличные результаты
        assertEquals(5, strategy.getGameRating(120, 32))
        assertEquals(5, strategy.getGameRating(90, 28))
        
        // Хорошие результаты
        assertEquals(4, strategy.getGameRating(240, 45))
        assertEquals(4, strategy.getGameRating(180, 40))
        
        // Средние результаты
        assertEquals(3, strategy.getGameRating(480, 60))
        assertEquals(3, strategy.getGameRating(360, 55))
        
        // Плохие результаты
        assertEquals(2, strategy.getGameRating(720, 80))
        assertEquals(2, strategy.getGameRating(600, 75))
        
        // Очень плохие результаты
        assertEquals(1, strategy.getGameRating(800, 90))
        assertEquals(1, strategy.getGameRating(1000, 100))
    }

    @Test
    fun `test DifficultyManager get strategy`() {
        val easyStrategy = DifficultyManager.getStrategy(4)
        val mediumStrategy = DifficultyManager.getStrategy(6)
        val hardStrategy = DifficultyManager.getStrategy(8)
        
        assertTrue(easyStrategy is EasyDifficultyStrategy)
        assertTrue(mediumStrategy is MediumDifficultyStrategy)
        assertTrue(hardStrategy is HardDifficultyStrategy)
    }

    @Test
    fun `test DifficultyManager get current strategy`() {
        DifficultyManager.setStrategy(4)
        val currentStrategy = DifficultyManager.getCurrentStrategy()
        assertTrue(currentStrategy is EasyDifficultyStrategy)
        
        DifficultyManager.setStrategy(6)
        val currentStrategy2 = DifficultyManager.getCurrentStrategy()
        assertTrue(currentStrategy2 is MediumDifficultyStrategy)
        
        DifficultyManager.setStrategy(8)
        val currentStrategy3 = DifficultyManager.getCurrentStrategy()
        assertTrue(currentStrategy3 is HardDifficultyStrategy)
    }

    @Test
    fun `test DifficultyManager get all strategies`() {
        val strategies = DifficultyManager.getAllStrategies()
        
        assertEquals(3, strategies.size)
        assertTrue(strategies.containsKey(4))
        assertTrue(strategies.containsKey(6))
        assertTrue(strategies.containsKey(8))
        
        assertTrue(strategies[4] is EasyDifficultyStrategy)
        assertTrue(strategies[6] is MediumDifficultyStrategy)
        assertTrue(strategies[8] is HardDifficultyStrategy)
    }

    @Test
    fun `test DifficultyManager get strategy info`() {
        val info = DifficultyManager.getStrategyInfo()
        
        assertTrue(info.contains("Easy"))
        assertTrue(info.contains("Medium"))
        assertTrue(info.contains("Hard"))
        assertTrue(info.contains("4x4"))
        assertTrue(info.contains("6x6"))
        assertTrue(info.contains("8x8"))
    }
    
    @Test
    fun `test DifficultyStrategy getGameRating with different conditions`() {
        // Тестируем различные условия для получения рейтинга
        
        // Easy стратегия
        val easyStrategy = EasyDifficultyStrategy()
        
        // Тест с отличным временем
        val excellentRating = easyStrategy.getGameRating(30, 5)
        assertTrue(excellentRating >= 4, "Отличное время должно давать высокий рейтинг")
        
        // Тест с плохим временем
        val poorRating = easyStrategy.getGameRating(300, 50)
        assertTrue(poorRating <= 2, "Плохое время должно давать низкий рейтинг")
        
        // Medium стратегия
        val mediumStrategy = MediumDifficultyStrategy()
        val mediumRating = mediumStrategy.getGameRating(60, 10)
        assertTrue(mediumRating >= 1, "Рейтинг должен быть не менее 1")
        
        // Hard стратегия
        val hardStrategy = HardDifficultyStrategy()
        val hardRating = hardStrategy.getGameRating(120, 20)
        assertTrue(hardRating >= 1, "Рейтинг должен быть не менее 1")
    }
    
    @Test
    fun `test DifficultyStrategy isGameWon with different matched pairs`() {
        // Тестируем различные количества совпавших пар
        
        val easyStrategy = EasyDifficultyStrategy()
        val mediumStrategy = MediumDifficultyStrategy()
        val hardStrategy = HardDifficultyStrategy()
        
        // Тест с полной победой
        assertTrue(easyStrategy.isGameWon(easyStrategy.totalPairs), "Easy: Полная победа должна быть true")
        assertTrue(mediumStrategy.isGameWon(mediumStrategy.totalPairs), "Medium: Полная победа должна быть true")
        assertTrue(hardStrategy.isGameWon(hardStrategy.totalPairs), "Hard: Полная победа должна быть true")
        
        // Тест с неполной победой
        assertFalse(easyStrategy.isGameWon(easyStrategy.totalPairs - 1), "Easy: Неполная победа должна быть false")
        assertFalse(mediumStrategy.isGameWon(mediumStrategy.totalPairs - 1), "Medium: Неполная победа должна быть false")
        assertFalse(hardStrategy.isGameWon(hardStrategy.totalPairs - 1), "Hard: Неполная победа должна быть false")
        
        // Тест с нулевыми парами
        assertFalse(easyStrategy.isGameWon(0), "Easy: Нулевые пары должны быть false")
        assertFalse(mediumStrategy.isGameWon(0), "Medium: Нулевые пары должны быть false")
        assertFalse(hardStrategy.isGameWon(0), "Hard: Нулевые пары должны быть false")
    }
    
    @Test
    fun `test DifficultyManager with edge cases`() {
        // Тестируем граничные случаи
        
        // Тест с отрицательным размером
        DifficultyManager.setStrategy(-1)
        assertEquals(4, DifficultyManager.getCurrentStrategy().gridSize, "Отрицательный размер должен fallback к Easy")
        
        // Тест с нулевым размером
        DifficultyManager.setStrategy(0)
        assertEquals(4, DifficultyManager.getCurrentStrategy().gridSize, "Нулевой размер должен fallback к Easy")
        
        // Тест с очень большим размером
        DifficultyManager.setStrategy(100)
        assertEquals(4, DifficultyManager.getCurrentStrategy().gridSize, "Очень большой размер должен fallback к Easy")
        
        // Тест с промежуточным размером
        DifficultyManager.setStrategy(5)
        assertEquals(4, DifficultyManager.getCurrentStrategy().gridSize, "Промежуточный размер должен fallback к Easy")
    }
    
    @Test
    fun `test DifficultyStrategy rating calculation with different time ranges`() {
        // Тестируем расчет рейтинга с различными диапазонами времени
        
        val easyStrategy = EasyDifficultyStrategy()
        val mediumStrategy = MediumDifficultyStrategy()
        val hardStrategy = HardDifficultyStrategy()
        
        // Тест с отличным временем (быстро)
        val excellentTimeEasy = easyStrategy.getGameRating(30, 5)
        val excellentTimeMedium = mediumStrategy.getGameRating(60, 10)
        val excellentTimeHard = hardStrategy.getGameRating(120, 20)
        
        assertTrue(excellentTimeEasy >= 4, "Easy: Отличное время должно давать высокий рейтинг")
        assertTrue(excellentTimeMedium >= 4, "Medium: Отличное время должно давать высокий рейтинг")
        assertTrue(excellentTimeHard >= 4, "Hard: Отличное время должно давать высокий рейтинг")
        
        // Тест с хорошим временем
        val goodTimeEasy = easyStrategy.getGameRating(60, 8)
        val goodTimeMedium = mediumStrategy.getGameRating(120, 15)
        val goodTimeHard = hardStrategy.getGameRating(240, 30)
        
        assertTrue(goodTimeEasy >= 3, "Easy: Хорошее время должно давать хороший рейтинг")
        assertTrue(goodTimeMedium >= 3, "Medium: Хорошее время должно давать хороший рейтинг")
        assertTrue(goodTimeHard >= 3, "Hard: Хорошее время должно давать хороший рейтинг")
        
        // Тест с средним временем
        val averageTimeEasy = easyStrategy.getGameRating(120, 12)
        val averageTimeMedium = mediumStrategy.getGameRating(240, 25)
        val averageTimeHard = hardStrategy.getGameRating(480, 50)
        
        assertTrue(averageTimeEasy >= 2, "Easy: Среднее время должно давать средний рейтинг")
        assertTrue(averageTimeMedium >= 2, "Medium: Среднее время должно давать средний рейтинг")
        assertTrue(averageTimeHard >= 2, "Hard: Среднее время должно давать средний рейтинг")
        
        // Тест с плохим временем
        val poorTimeEasy = easyStrategy.getGameRating(300, 30)
        val poorTimeMedium = mediumStrategy.getGameRating(600, 60)
        val poorTimeHard = hardStrategy.getGameRating(1200, 120)
        
        assertTrue(poorTimeEasy <= 2, "Easy: Плохое время должно давать низкий рейтинг")
        assertTrue(poorTimeMedium <= 2, "Medium: Плохое время должно давать низкий рейтинг")
        assertTrue(poorTimeHard <= 2, "Hard: Плохое время должно давать низкий рейтинг")
    }
    
    @Test
    fun `test DifficultyStrategy rating calculation with different attempt ranges`() {
        // Тестируем расчет рейтинга с различными диапазонами попыток
        
        val easyStrategy = EasyDifficultyStrategy()
        
        // Тест с минимальными попытками
        val minAttemptsRating = easyStrategy.getGameRating(60, 1)
        assertTrue(minAttemptsRating >= 4, "Минимальные попытки должны давать высокий рейтинг")
        
        // Тест с оптимальными попытками
        val optimalAttemptsRating = easyStrategy.getGameRating(60, 8)
        assertTrue(optimalAttemptsRating >= 3, "Оптимальные попытки должны давать хороший рейтинг")
        
        // Тест с средними попытками
        val averageAttemptsRating = easyStrategy.getGameRating(60, 16)
        assertTrue(averageAttemptsRating >= 2, "Средние попытки должны давать средний рейтинг")
        
        // Тест с большим количеством попыток
        val manyAttemptsRating = easyStrategy.getGameRating(60, 32)
        assertTrue(manyAttemptsRating <= 2, "Много попыток должны давать низкий рейтинг")
    }
    
    @Test
    fun `test DifficultyStrategy properties`() {
        // Тестируем свойства стратегий
        
        val easyStrategy = EasyDifficultyStrategy()
        val mediumStrategy = MediumDifficultyStrategy()
        val hardStrategy = HardDifficultyStrategy()
        
        // Проверяем размеры сетки
        assertEquals(4, easyStrategy.gridSize, "Easy должен иметь сетку 4x4")
        assertEquals(6, mediumStrategy.gridSize, "Medium должен иметь сетку 6x6")
        assertEquals(8, hardStrategy.gridSize, "Hard должен иметь сетку 8x8")
        
        // Проверяем количество пар
        assertEquals(8, easyStrategy.totalPairs, "Easy должен иметь 8 пар")
        assertEquals(18, mediumStrategy.totalPairs, "Medium должен иметь 18 пар")
        assertEquals(32, hardStrategy.totalPairs, "Hard должен иметь 32 пары")
        
        // Проверяем соответствие размера сетки и количества пар
        assertEquals((easyStrategy.gridSize * easyStrategy.gridSize) / 2, easyStrategy.totalPairs, "Easy: количество пар должно соответствовать размеру сетки")
        assertEquals((mediumStrategy.gridSize * mediumStrategy.gridSize) / 2, mediumStrategy.totalPairs, "Medium: количество пар должно соответствовать размеру сетки")
        assertEquals((hardStrategy.gridSize * hardStrategy.gridSize) / 2, hardStrategy.totalPairs, "Hard: количество пар должно соответствовать размеру сетки")
    }
    
    @Test
    fun `test DifficultyManager strategy switching`() {
        // Тестируем переключение стратегий
        
        // Устанавливаем Easy стратегию
        DifficultyManager.setStrategy(4)
        val easyStrategy = DifficultyManager.getCurrentStrategy()
        assertEquals(4, easyStrategy.gridSize, "Должна быть установлена Easy стратегия")
        
        // Устанавливаем Medium стратегию
        DifficultyManager.setStrategy(6)
        val mediumStrategy = DifficultyManager.getCurrentStrategy()
        assertEquals(6, mediumStrategy.gridSize, "Должна быть установлена Medium стратегия")
        
        // Устанавливаем Hard стратегию
        DifficultyManager.setStrategy(8)
        val hardStrategy = DifficultyManager.getCurrentStrategy()
        assertEquals(8, hardStrategy.gridSize, "Должна быть установлена Hard стратегия")
        
        // Возвращаемся к Easy
        DifficultyManager.setStrategy(4)
        val backToEasyStrategy = DifficultyManager.getCurrentStrategy()
        assertEquals(4, backToEasyStrategy.gridSize, "Должна быть возвращена Easy стратегия")
    }
    
    @Test
    fun `test DifficultyStrategy rating boundary conditions`() {
        // Тестируем граничные условия для рейтинга
        
        val easyStrategy = EasyDifficultyStrategy()
        
        // Тест с минимальными значениями
        val minRating = easyStrategy.getGameRating(1, 1)
        assertTrue(minRating >= 1 && minRating <= 5, "Минимальные значения должны давать рейтинг от 1 до 5")
        
        // Тест с максимальными значениями
        val maxRating = easyStrategy.getGameRating(Int.MAX_VALUE, Int.MAX_VALUE)
        assertTrue(maxRating >= 1 && maxRating <= 5, "Максимальные значения должны давать рейтинг от 1 до 5")
        
        // Тест с отрицательными значениями (если поддерживается)
        val negativeRating = easyStrategy.getGameRating(-1, -1)
        assertTrue(negativeRating >= 1 && negativeRating <= 5, "Отрицательные значения должны давать рейтинг от 1 до 5")
    }
    
    @Test
    fun `test DifficultyStrategy isGameWon boundary conditions`() {
        // Тестируем граничные условия для isGameWon
        
        val easyStrategy = EasyDifficultyStrategy()
        val mediumStrategy = MediumDifficultyStrategy()
        val hardStrategy = HardDifficultyStrategy()
        
        // Тест с отрицательным количеством пар - проверяем что метод не падает
        assertDoesNotThrow {
            easyStrategy.isGameWon(-1)
            mediumStrategy.isGameWon(-1)
            hardStrategy.isGameWon(-1)
        }
        
        // Тест с количеством пар больше максимального - должно быть true (>= totalPairs)
        assertTrue(easyStrategy.isGameWon(easyStrategy.totalPairs + 1), "Easy: Больше максимального должно быть true")
        assertTrue(mediumStrategy.isGameWon(mediumStrategy.totalPairs + 1), "Medium: Больше максимального должно быть true")
        assertTrue(hardStrategy.isGameWon(hardStrategy.totalPairs + 1), "Hard: Больше максимального должно быть true")
        
        // Тест с максимальным количеством пар
        assertTrue(easyStrategy.isGameWon(easyStrategy.totalPairs), "Easy: Максимальное количество должно быть true")
        assertTrue(mediumStrategy.isGameWon(mediumStrategy.totalPairs), "Medium: Максимальное количество должно быть true")
        assertTrue(hardStrategy.isGameWon(hardStrategy.totalPairs), "Hard: Максимальное количество должно быть true")
    }
}
