package com.memorygame.backend

/**
 * SettingsManager - Singleton для управления настройками игры
 * 
 * Применяет паттерн Singleton для централизованного управления
 * всеми настройками игры.
 * 
 * Решает проблему: Обеспечивает единую точку доступа к настройкам
 * и их синхронизацию между компонентами.
 */
object SettingsManager {
    
    // Настройки игры
    private var _animationsEnabled = true
    private var _soundEnabled = true
    private var _currentTheme = "dark"
    private var _difficulty = 4
    
    // Статистика
    private var _gamesPlayed = 0
    private var _bestTime = Int.MAX_VALUE
    private var _totalMatches = 0
    private val _achievements = mutableSetOf<String>()
    
    // Getters для настроек
    val animationsEnabled: Boolean get() = _animationsEnabled
    val soundEnabled: Boolean get() = _soundEnabled
    val currentTheme: String get() = _currentTheme
    val difficulty: Int get() = _difficulty
    
    // Getters для статистики
    val gamesPlayed: Int get() = _gamesPlayed
    val bestTime: Int get() = _bestTime
    val totalMatches: Int get() = _totalMatches
    val achievements: Set<String> get() = _achievements.toSet()
    
    /**
     * Переключает состояние анимаций
     */
    fun toggleAnimations() {
        _animationsEnabled = !_animationsEnabled
    }
    
    /**
     * Переключает состояние звука
     */
    fun toggleSound() {
        _soundEnabled = !_soundEnabled
    }
    
    /**
     * Устанавливает тему оформления
     * @param theme название темы ("dark" или "light")
     */
    fun setTheme(theme: String) {
        if (theme in listOf("dark", "light")) {
            _currentTheme = theme
        }
    }
    
    /**
     * Устанавливает уровень сложности
     * @param level уровень сложности (4, 6 или 8)
     */
    fun setDifficulty(level: Int) {
        if (level in listOf(4, 6, 8)) {
            _difficulty = level
        }
    }
    
    /**
     * Обновляет статистику игры
     * @param time время игры в секундах
     * @param matches количество совпадений
     */
    fun updateGameStats(time: Int, matches: Int) {
        _gamesPlayed++
        _totalMatches += matches
        
        if (time < _bestTime) {
            _bestTime = time
        }
        
        // Проверяем достижения
        checkAchievements(time)
    }
    
    /**
     * Добавляет достижение
     * @param achievement название достижения
     */
    fun addAchievement(achievement: String) {
        _achievements.add(achievement)
    }
    
    /**
     * Проверяет и добавляет достижения
     */
    private fun checkAchievements(time: Int) {
        // Проверяем достижения по количеству игр
        when (_gamesPlayed) {
            1 -> addAchievement("🎮 Первая игра!")
            10 -> addAchievement("🔥 10 игр сыграно!")
            50 -> addAchievement("💎 50 игр сыграно!")
            100 -> addAchievement("👑 100 игр сыграно!")
        }
        
        // Проверяем достижения по времени
        when {
            time <= 30 -> addAchievement("⚡ Молниеносная победа!")
            time <= 60 -> addAchievement("🚀 Быстрая победа!")
        }
        
        // Проверяем достижения по совпадениям
        when {
            _totalMatches >= 500 -> addAchievement("🎊 500 совпадений!")
            _totalMatches >= 100 -> addAchievement("💯 100 совпадений!")
        }
    }
    
    /**
     * Сбрасывает статистику
     */
    fun resetStats() {
        _gamesPlayed = 0
        _bestTime = Int.MAX_VALUE
        _totalMatches = 0
        _achievements.clear()
    }
    
    /**
     * Сбрасывает настройки к значениям по умолчанию
     */
    fun resetToDefaults() {
        _animationsEnabled = true
        _soundEnabled = true
        _currentTheme = "dark"
        _difficulty = 4
    }
    
    /**
     * Получает строковое представление настроек
     */
    fun getSettingsString(): String {
        return """
            Animations: ${if (_animationsEnabled) "ON" else "OFF"}
            Sound: ${if (_soundEnabled) "ON" else "OFF"}
            Theme: $_currentTheme
            Difficulty: ${_difficulty}x$_difficulty
            Games played: $_gamesPlayed
            Best time: ${if (_bestTime == Int.MAX_VALUE) "None" else formatTime(_bestTime)}
            Total matches: $_totalMatches
            Achievements: ${_achievements.size}
        """.trimIndent()
    }
    
    /**
     * Форматирует время в MM:SS
     */
    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
}
