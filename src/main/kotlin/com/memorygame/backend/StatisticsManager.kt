package com.memorygame.backend

import com.memorygame.data.GameSession
import com.memorygame.data.PlayerProfile
import com.memorygame.data.StatisticsData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * StatisticsManager - Singleton для управления статистикой игроков
 * 
 * Применяет паттерн Singleton для централизованного управления
 * статистикой и персистентностью данных.
 * 
 * Решает проблему: Обеспечивает единую точку доступа к статистике
 * и её сохранению между сеансами игры.
 * 
 * Функциональность:
 * - Сохранение и загрузка статистики из JSON файла
 * - Управление профилями игроков
 * - Отслеживание игровых сессий
 * - Получение статистики и рекордов
 */
object StatisticsManager {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    private var statisticsData: StatisticsData = StatisticsData()
    private var dataDirectory: File? = null
    private var statisticsFile: File? = null
    
    /**
     * Инициализирует менеджер статистики
     * Создает директорию для данных и загружает существующую статистику
     */
    fun initialize(): Boolean {
        try {
            // Определяем директорию для данных
            val userHome = System.getProperty("user.home")
            val dataDir = File(userHome, ".memorygame")
            
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            
            dataDirectory = dataDir
            statisticsFile = File(dataDir, "statistics.json")
            
            // Загружаем существующую статистику
            loadStatistics()
            
            return true
        } catch (e: Exception) {
            println("Ошибка инициализации StatisticsManager: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Загружает статистику из файла
     */
    fun loadStatistics(): Boolean {
        return try {
            val file = statisticsFile ?: return false
            
            if (!file.exists()) {
                // Файл не существует - создаем новый
                statisticsData = StatisticsData()
                saveStatistics()
                return true
            }
            
            val jsonString = file.readText(Charsets.UTF_8)
            if (jsonString.isBlank()) {
                statisticsData = StatisticsData()
                return true
            }
            
            val loadedData = json.decodeFromString<StatisticsData>(jsonString)
            // Загруженные данные автоматически конвертируются в мутабельные коллекции в конструкторе
            statisticsData = loadedData
            statisticsData.updateTimestamp()
            
            true
        } catch (e: Exception) {
            println("Ошибка загрузки статистики: ${e.message}")
            e.printStackTrace()
            // При ошибке создаем новый объект статистики
            statisticsData = StatisticsData()
            false
        }
    }
    
    /**
     * Сохраняет статистику в файл
     */
    fun saveStatistics(): Boolean {
        return try {
            val file = statisticsFile ?: return false
            
            statisticsData.updateTimestamp()
            // Создаем сериализуемую версию данных
            val serializableData = statisticsData.toSerializable()
            val jsonString = json.encodeToString(serializableData)
            
            // Создаем резервную копию перед сохранением
            if (file.exists()) {
                val backupFile = File(file.parent, "statistics.json.backup")
                file.copyTo(backupFile, overwrite = true)
            }
            
            file.writeText(jsonString, Charsets.UTF_8)
            
            true
        } catch (e: Exception) {
            println("Ошибка сохранения статистики: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Получает текущего игрока
     */
    fun getCurrentPlayer(): PlayerProfile? {
        return statisticsData.getCurrentPlayerProfile()
    }
    
    /**
     * Устанавливает текущего игрока
     */
    fun setCurrentPlayer(name: String): Boolean {
        val result = statisticsData.setCurrentPlayer(name)
        if (result) {
            saveStatistics()
        }
        return result
    }
    
    /**
     * Создает нового игрока
     */
    fun createPlayer(name: String): PlayerProfile? {
        val profile = statisticsData.createPlayer(name)
        if (profile != null) {
            saveStatistics()
        }
        return profile
    }
    
    /**
     * Получает профиль игрока
     */
    fun getPlayerProfile(name: String): PlayerProfile? {
        return statisticsData.getPlayer(name)
    }
    
    /**
     * Получает список всех игроков
     */
    fun getAllPlayers(): List<PlayerProfile> {
        return statisticsData.getAllPlayers()
    }
    
    /**
     * Удаляет игрока
     */
    fun removePlayer(name: String): Boolean {
        val result = statisticsData.removePlayer(name)
        if (result) {
            saveStatistics()
        }
        return result
    }
    
    /**
     * Обновляет статистику игрока после завершения игры
     */
    fun updatePlayerStats(gameSession: GameSession) {
        val playerName = gameSession.playerName
        var profile = statisticsData.getPlayer(playerName)
        
        if (profile == null) {
            // Создаем профиль, если его нет
            profile = statisticsData.createPlayer(playerName)
        }
        
        if (profile != null) {
            profile.addGameSession(gameSession)
            saveStatistics()
        }
    }
    
    /**
     * Получает историю игр игрока
     */
    fun getPlayerGameHistory(name: String, limit: Int = 50): List<GameSession> {
        val profile = statisticsData.getPlayer(name) ?: return emptyList()
        return profile.getGameSessionsList().takeLast(limit).reversed()
    }
    
    /**
     * Получает рекорды игрока
     */
    fun getPlayerRecords(name: String): Map<String, Any> {
        val profile = statisticsData.getPlayer(name) ?: return emptyMap()
        
        return mapOf(
            "bestTime" to profile.bestTime,
            "bestTimeDifficulty" to profile.bestTimeDifficulty,
            "bestTimeFormatted" to profile.getBestTimeFormatted(),
            "totalGames" to profile.totalGames,
            "wonGames" to profile.wonGames,
            "winRate" to profile.getWinRate(),
            "totalAttempts" to profile.totalAttempts,
            "averageAttempts" to profile.getAverageAttempts(),
            "totalMatches" to profile.totalMatches,
            "achievements" to profile.getAchievementsSet().toList(),
            "bestTimesByDifficulty" to profile.getBestTimesByDifficulty()
        )
    }
    
    /**
     * Получает текущие данные статистики
     */
    fun getStatisticsData(): StatisticsData {
        return statisticsData
    }
    
    /**
     * Проверяет, существует ли игрок
     */
    fun playerExists(name: String): Boolean {
        return statisticsData.playerExists(name)
    }
    
    /**
     * Получает путь к файлу статистики
     */
    fun getStatisticsFilePath(): String? {
        return statisticsFile?.absolutePath
    }
    
    /**
     * Устанавливает тестовые пути для изоляции тестов
     * Только для использования в тестах!
     */
    internal fun setTestPaths(dataDir: File, statsFile: File) {
        dataDirectory = dataDir
        statisticsFile = statsFile
        statisticsData = StatisticsData()
    }
    
    /**
     * Сбрасывает состояние StatisticsManager для тестов
     * Только для использования в тестах!
     */
    internal fun resetForTesting() {
        statisticsData = StatisticsData()
        dataDirectory = null
        statisticsFile = null
    }
}

