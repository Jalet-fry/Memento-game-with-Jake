package com.memorygame.logic

import com.memorygame.backend.DifficultyManager

/**
 * GameLogic - класс для бизнес-логики игры
 * 
 * Содержит всю логику игры, не связанную с UI.
 * Это позволяет легко тестировать игровую логику отдельно от GUI.
 */
class GameLogic {
    
    // Игровая статистика
    private var _matchedPairs = 0
    private var _attempts = 0
    private var _elapsedSeconds = 0
    
    // Getters для статистики
    val matchedPairs: Int get() = _matchedPairs
    val attempts: Int get() = _attempts
    val elapsedSeconds: Int get() = _elapsedSeconds
    
    /**
     * Увеличивает счетчик совпавших пар
     */
    fun incrementMatchedPairs() {
        _matchedPairs++
    }
    
    /**
     * Увеличивает счетчик попыток
     */
    fun incrementAttempts() {
        _attempts++
    }
    
    /**
     * Проверяет совпадение двух карточек
     * @param card1Id ID первой карточки
     * @param card2Id ID второй карточки
     * @return true если карточки совпадают
     */
    fun checkMatch(card1Id: Int, card2Id: Int): Boolean {
        _attempts++
        return if (card1Id == card2Id) {
            _matchedPairs++
            true
        } else {
            false
        }
    }
    
    /**
     * Проверяет условие победы
     * @return true если игра выиграна
     */
    fun checkWin(): Boolean {
        val strategy = DifficultyManager.getCurrentStrategy()
        return strategy.isGameWon(_matchedPairs)
    }
    
    /**
     * Обновляет время игры
     * @param seconds новое время в секундах
     */
    fun updateTime(seconds: Int) {
        _elapsedSeconds = seconds
    }
    
    /**
     * Увеличивает время на 1 секунду
     */
    fun incrementTime() {
        _elapsedSeconds++
    }
    
    /**
     * Сбрасывает игру к начальному состоянию
     */
    fun resetGame() {
        _matchedPairs = 0
        _attempts = 0
        _elapsedSeconds = 0
    }
    
    /**
     * Форматирует время в MM:SS
     * @param seconds время в секундах
     * @return отформатированная строка времени
     */
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    /**
     * Получает отформатированное текущее время
     */
    fun getFormattedTime(): String {
        return formatTime(_elapsedSeconds)
    }
    
    /**
     * Получает информацию о текущем состоянии игры
     */
    fun getGameStateInfo(): String {
        val strategy = DifficultyManager.getCurrentStrategy()
        return """
            Попытки: $_attempts
            Совпадения: $_matchedPairs/${strategy.totalPairs}
            Время: ${getFormattedTime()}
        """.trimIndent()
    }
    
    /**
     * Проверяет, можно ли сделать ход (не занят ли игровой процесс)
     * @param isBusy флаг занятости
     * @return true если можно сделать ход
     */
    fun canMakeMove(isBusy: Boolean): Boolean {
        return !isBusy
    }
    
    /**
     * Получает рейтинг игры
     * @return рейтинг от 1 до 5 звезд
     */
    fun getGameRating(): Int {
        val strategy = DifficultyManager.getCurrentStrategy()
        return strategy.getGameRating(_elapsedSeconds, _attempts)
    }
    
    /**
     * Проверяет, является ли игра завершенной
     * @return true если игра завершена (выиграна)
     */
    fun isGameCompleted(): Boolean {
        return checkWin()
    }
}
