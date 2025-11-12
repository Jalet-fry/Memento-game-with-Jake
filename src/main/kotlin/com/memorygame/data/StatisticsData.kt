package com.memorygame.data

import kotlinx.serialization.Serializable

/**
 * StatisticsData - корневой объект данных статистики
 * 
 * Хранит все данные статистики:
 * - Карта профилей игроков
 * - Текущий игрок
 * - Версия приложения
 * - Дата последнего обновления
 * 
 * Применяет паттерн Data Class для хранения всех данных статистики.
 */
@Serializable
data class StatisticsData(
    val players: Map<String, PlayerProfile> = emptyMap(),
    var currentPlayer: String? = null,
    val appVersion: String = "1.0",
    var lastUpdated: Long = System.currentTimeMillis()
) {
    // Внутренняя мутабельная карта для работы
    // При создании из сериализованных данных создаем новые экземпляры с мутабельными коллекциями
    private val _players: MutableMap<String, PlayerProfile> = if (players.isEmpty()) {
        mutableMapOf()
    } else {
        players.mapValues { (_, profile) ->
            // Создаем новый экземпляр с мутабельными коллекциями из сериализованных данных
            PlayerProfile(
                name = profile.name,
                totalGames = profile.totalGames,
                wonGames = profile.wonGames,
                bestTime = profile.bestTime,
                bestTimeDifficulty = profile.bestTimeDifficulty,
                totalAttempts = profile.totalAttempts,
                totalMatches = profile.totalMatches,
                achievements = profile.achievements, // Set будет конвертирован в MutableSet внутри конструктора
                gameSessions = profile.gameSessions, // List будет конвертирован в MutableList внутри конструктора
                createdAt = profile.createdAt,
                lastPlayed = profile.lastPlayed
            )
        }.toMutableMap()
    }
    
    /**
     * Получает текущего игрока
     */
    fun getCurrentPlayerProfile(): PlayerProfile? {
        return currentPlayer?.let { _players[it] }
    }
    
    /**
     * Устанавливает текущего игрока
     */
    fun setCurrentPlayer(name: String): Boolean {
        return if (_players.containsKey(name)) {
            currentPlayer = name
            true
        } else {
            false
        }
    }
    
    /**
     * Создает нового игрока
     */
    fun createPlayer(name: String): PlayerProfile? {
        return if (name.isNotBlank() && !_players.containsKey(name)) {
            val profile = PlayerProfile(name = name)
            _players[name] = profile
            if (currentPlayer == null) {
                currentPlayer = name
            }
            profile
        } else {
            null
        }
    }
    
    /**
     * Получает список всех игроков
     */
    fun getAllPlayers(): List<PlayerProfile> {
        return _players.values.sortedByDescending { it.lastPlayed }
    }
    
    /**
     * Удаляет игрока
     */
    fun removePlayer(name: String): Boolean {
        return if (_players.containsKey(name)) {
            _players.remove(name)
            if (currentPlayer == name) {
                currentPlayer = _players.keys.firstOrNull()
            }
            true
        } else {
            false
        }
    }
    
    /**
     * Получает профиль игрока
     */
    fun getPlayer(name: String): PlayerProfile? {
        return _players[name]
    }
    
    /**
     * Проверяет, существует ли игрок
     */
    fun playerExists(name: String): Boolean {
        return _players.containsKey(name)
    }
    
    /**
     * Обновляет время последнего обновления
     */
    fun updateTimestamp() {
        lastUpdated = System.currentTimeMillis()
    }
    
    /**
     * Создает копию данных с обновленными коллекциями для сериализации
     */
    fun toSerializable(): StatisticsData {
        return copy(
            players = _players.mapValues { it.value.toSerializable() }
        )
    }
    
    /**
     * Получает внутреннюю карту игроков (для StatisticsManager)
     */
    fun getPlayersMap(): MutableMap<String, PlayerProfile> {
        return _players
    }
}
