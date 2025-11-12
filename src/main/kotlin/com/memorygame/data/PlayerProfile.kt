package com.memorygame.data

import kotlinx.serialization.Serializable

/**
 * PlayerProfile - профиль игрока
 * 
 * Хранит статистику игрока:
 * - Имя игрока
 * - Общее количество игр
 * - Количество выигранных игр
 * - Лучшее время
 * - Сложность лучшего времени
 * - Общее количество попыток
 * - Общее количество совпадений
 * - Достижения
 * - История игр
 * - Дата создания профиля
 * - Дата последней игры
 * 
 * Применяет паттерн Data Class для хранения данных профиля игрока.
 */
@Serializable
data class PlayerProfile(
    val name: String,
    var totalGames: Int = 0,
    var wonGames: Int = 0,
    var bestTime: Int = Int.MAX_VALUE,
    var bestTimeDifficulty: Int = 4,
    var totalAttempts: Int = 0,
    var totalMatches: Int = 0,
    val achievements: Set<String> = emptySet(),
    val gameSessions: List<GameSession> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    var lastPlayed: Long = System.currentTimeMillis()
) {
    // Внутренние мутабельные коллекции для работы
    private val _achievements: MutableSet<String> = achievements.toMutableSet()
    private val _gameSessions: MutableList<GameSession> = gameSessions.toMutableList()
    
    /**
     * Получает достижения как Set
     */
    fun getAchievementsSet(): Set<String> = _achievements.toSet()
    
    /**
     * Получает игровые сессии как List
     */
    fun getGameSessionsList(): List<GameSession> = _gameSessions.toList()
    
    /**
     * Получает процент выигрышей
     */
    fun getWinRate(): Double {
        return if (totalGames > 0) {
            (wonGames.toDouble() / totalGames.toDouble()) * 100.0
        } else {
            0.0
        }
    }
    
    /**
     * Получает среднее количество попыток
     */
    fun getAverageAttempts(): Double {
        return if (totalGames > 0) {
            totalAttempts.toDouble() / totalGames.toDouble()
        } else {
            0.0
        }
    }
    
    /**
     * Получает лучшее время в формате MM:SS
     */
    fun getBestTimeFormatted(): String {
        return if (bestTime == Int.MAX_VALUE) {
            "Нет"
        } else {
            val mins = bestTime / 60
            val secs = bestTime % 60
            String.format("%02d:%02d", mins, secs)
        }
    }
    
    /**
     * Получает последние N игр
     */
    fun getRecentGames(count: Int = 10): List<GameSession> {
        return _gameSessions.takeLast(count).reversed()
    }
    
    /**
     * Получает рекорды по уровням сложности
     */
    fun getBestTimesByDifficulty(): Map<Int, Int> {
        val bestTimes = mutableMapOf<Int, Int>()
        _gameSessions.filter { it.won }.forEach { session ->
            val currentBest = bestTimes[session.difficulty] ?: Int.MAX_VALUE
            if (session.time < currentBest) {
                bestTimes[session.difficulty] = session.time
            }
        }
        return bestTimes
    }
    
    /**
     * Добавляет игровую сессию и обновляет статистику
     */
    fun addGameSession(session: GameSession) {
        _gameSessions.add(session)
        totalGames++
        if (session.won) {
            wonGames++
        }
        totalAttempts += session.attempts
        totalMatches += session.matchedPairs
        lastPlayed = session.date
        
        // Обновляем лучшее время
        if (session.won && session.time < bestTime) {
            bestTime = session.time
            bestTimeDifficulty = session.difficulty
        }
        
        // Проверяем достижения
        checkAchievements(session)
    }
    
    /**
     * Добавляет достижение
     */
    fun addAchievement(achievement: String) {
        _achievements.add(achievement)
    }
    
    /**
     * Проверяет и добавляет достижения
     */
    private fun checkAchievements(session: GameSession) {
        // Проверяем достижения по количеству игр
        when (totalGames) {
            1 -> addAchievement("🎮 Первая игра!")
            10 -> addAchievement("🔥 10 игр сыграно!")
            50 -> addAchievement("💎 50 игр сыграно!")
            100 -> addAchievement("👑 100 игр сыграно!")
        }
        
        // Проверяем достижения по времени
        if (session.won) {
            when {
                session.time <= 30 -> addAchievement("⚡ Молниеносная победа!")
                session.time <= 60 -> addAchievement("🚀 Быстрая победа!")
            }
        }
        
        // Проверяем достижения по совпадениям
        when {
            totalMatches >= 500 -> addAchievement("🎊 500 совпадений!")
            totalMatches >= 100 -> addAchievement("💯 100 совпадений!")
        }
    }
    
    /**
     * Создает копию профиля с обновленными коллекциями для сериализации
     */
    fun toSerializable(): PlayerProfile {
        return copy(
            achievements = _achievements.toSet(),
            gameSessions = _gameSessions.toList(),
            totalGames = totalGames,
            wonGames = wonGames,
            bestTime = bestTime,
            bestTimeDifficulty = bestTimeDifficulty,
            totalAttempts = totalAttempts,
            totalMatches = totalMatches,
            lastPlayed = lastPlayed
        )
    }
}

