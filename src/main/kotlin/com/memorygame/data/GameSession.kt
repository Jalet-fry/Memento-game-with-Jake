package com.memorygame.data

import kotlinx.serialization.Serializable

/**
 * GameSession - данные одной игровой сессии
 * 
 * Хранит информацию о завершенной игре:
 * - Имя игрока
 * - Уровень сложности
 * - Время игры
 * - Количество попыток
 * - Количество найденных пар
 * - Дата и время игры
 * - Результат (выиграна/проиграна)
 * - Рейтинг игры
 * 
 * Применяет паттерн Data Class для хранения данных игровой сессии.
 */
@Serializable
data class GameSession(
    val playerName: String,
    val difficulty: Int,
    val time: Int, // в секундах
    val attempts: Int,
    val matchedPairs: Int,
    val date: Long, // timestamp
    val won: Boolean,
    val rating: Int // 1-5 звезд
) {
    /**
     * Форматирует время в MM:SS
     */
    fun getFormattedTime(): String {
        val mins = time / 60
        val secs = time % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    /**
     * Получает строковое представление рейтинга
     */
    fun getRatingStars(): String {
        return "⭐".repeat(rating)
    }
    
    /**
     * Получает строковое представление сложности
     */
    fun getDifficultyString(): String {
        return "${difficulty}x${difficulty}"
    }
}

