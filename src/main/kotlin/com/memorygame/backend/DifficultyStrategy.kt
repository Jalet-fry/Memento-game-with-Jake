package com.memorygame.backend

/**
 * DifficultyStrategy - интерфейс стратегии сложности игры
 * 
 * Применяет паттерн Strategy для различных уровней сложности игры.
 * 
 * Решает проблему: Позволяет легко добавлять новые уровни сложности
 * и изменять игровую логику без модификации основного кода.
 */
interface DifficultyStrategy {
    /**
     * Размер сетки игры
     */
    val gridSize: Int
    
    /**
     * Количество пар карточек
     */
    val totalPairs: Int
    
    /**
     * Название уровня сложности
     */
    val name: String
    
    /**
     * Описание уровня сложности
     */
    val description: String
    
    /**
     * Время на игру в секундах (0 = без ограничений)
     */
    val timeLimit: Int
    
    /**
     * Максимальное количество попыток (0 = без ограничений)
     */
    val maxAttempts: Int
    
    /**
     * Проверяет, выиграл ли игрок
     * @param matchedPairs количество совпавших пар
     * @return true если игрок выиграл
     */
    fun isGameWon(matchedPairs: Int): Boolean
    
    /**
     * Получает оценку результата игры
     * @param time время игры в секундах
     * @param attempts количество попыток
     * @return оценка от 1 до 5 звезд
     */
    fun getGameRating(time: Int, attempts: Int): Int
}

/**
 * EasyDifficultyStrategy - стратегия легкой сложности (4x4)
 */
class EasyDifficultyStrategy : DifficultyStrategy {
    override val gridSize: Int = 4
    override val totalPairs: Int = 8
    override val name: String = "Легко"
    override val description: String = "4x4 сетка, 8 пар карточек"
    override val timeLimit: Int = 0
    override val maxAttempts: Int = 0
    
    override fun isGameWon(matchedPairs: Int): Boolean {
        return matchedPairs >= totalPairs
    }
    
    override fun getGameRating(time: Int, attempts: Int): Int {
        return when {
            time <= 30 && attempts <= 8 -> 5
            time <= 60 && attempts <= 12 -> 4
            time <= 120 && attempts <= 16 -> 3
            time <= 180 && attempts <= 20 -> 2
            else -> 1
        }
    }
}

/**
 * MediumDifficultyStrategy - стратегия средней сложности (6x6)
 */
class MediumDifficultyStrategy : DifficultyStrategy {
    override val gridSize: Int = 6
    override val totalPairs: Int = 18
    override val name: String = "Средне"
    override val description: String = "6x6 сетка, 18 пар карточек"
    override val timeLimit: Int = 0
    override val maxAttempts: Int = 0
    
    override fun isGameWon(matchedPairs: Int): Boolean {
        return matchedPairs >= totalPairs
    }
    
    override fun getGameRating(time: Int, attempts: Int): Int {
        return when {
            time <= 60 && attempts <= 18 -> 5
            time <= 120 && attempts <= 25 -> 4
            time <= 240 && attempts <= 35 -> 3
            time <= 360 && attempts <= 45 -> 2
            else -> 1
        }
    }
}

/**
 * HardDifficultyStrategy - стратегия сложной сложности (8x8)
 */
class HardDifficultyStrategy : DifficultyStrategy {
    override val gridSize: Int = 8
    override val totalPairs: Int = 32
    override val name: String = "Сложно"
    override val description: String = "8x8 сетка, 32 пары карточек"
    override val timeLimit: Int = 0
    override val maxAttempts: Int = 0
    
    override fun isGameWon(matchedPairs: Int): Boolean {
        return matchedPairs >= totalPairs
    }
    
    override fun getGameRating(time: Int, attempts: Int): Int {
        return when {
            time <= 120 && attempts <= 32 -> 5
            time <= 240 && attempts <= 45 -> 4
            time <= 480 && attempts <= 60 -> 3
            time <= 720 && attempts <= 80 -> 2
            else -> 1
        }
    }
}

/**
 * DifficultyManager - менеджер стратегий сложности
 * 
 * Управляет выбором и переключением стратегий сложности
 */
object DifficultyManager {
    
    private val strategies = mapOf(
        4 to EasyDifficultyStrategy(),
        6 to MediumDifficultyStrategy(),
        8 to HardDifficultyStrategy()
    )
    
    private var currentStrategy: DifficultyStrategy = strategies[4]!!
    
    /**
     * Получает текущую стратегию
     */
    fun getCurrentStrategy(): DifficultyStrategy = currentStrategy
    
    /**
     * Устанавливает стратегию по размеру сетки
     * @param gridSize размер сетки (4, 6 или 8)
     */
    fun setStrategy(gridSize: Int) {
        currentStrategy = strategies[gridSize] ?: strategies[4]!!
    }
    
    /**
     * Получает все доступные стратегии
     */
    fun getAllStrategies(): Map<Int, DifficultyStrategy> = strategies
    
    /**
     * Получает стратегию по размеру сетки
     * @param gridSize размер сетки
     * @return стратегия или стратегия по умолчанию
     */
    fun getStrategy(gridSize: Int): DifficultyStrategy {
        return strategies[gridSize] ?: strategies[4]!!
    }
    
    /**
     * Получает информацию о текущей стратегии
     */
    fun getCurrentStrategyInfo(): String {
        return """
            Текущая сложность: ${currentStrategy.name}
            Описание: ${currentStrategy.description}
            Сетка: ${currentStrategy.gridSize}x${currentStrategy.gridSize}
            Пар карточек: ${currentStrategy.totalPairs}
        """.trimIndent()
    }
    
    /**
     * Получает информацию о всех стратегиях
     */
    fun getStrategyInfo(): String {
        return """
            Доступные уровни сложности:
            Easy: 4x4 сетка, 8 пар карточек
            Medium: 6x6 сетка, 18 пар карточек
            Hard: 8x8 сетка, 32 пары карточек
        """.trimIndent()
    }
}
