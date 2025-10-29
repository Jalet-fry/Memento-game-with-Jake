package com.memorygame.unit.backend

import com.memorygame.backend.SettingsManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для SettingsManager
 * 
 * Тестирует Singleton паттерн для управления настройками игры.
 * Покрывает все методы и функциональность менеджера настроек.
 */
class SettingsManagerTest {

    @BeforeEach
    fun setUp() {
        // Сбрасываем настройки перед каждым тестом
        SettingsManager.resetToDefaults()
        SettingsManager.resetStats()
    }

    @Test
    fun `test initial default settings`() {
        // Проверяем начальные настройки по умолчанию
        assertTrue(SettingsManager.animationsEnabled)
        assertTrue(SettingsManager.soundEnabled)
        assertEquals("dark", SettingsManager.currentTheme)
        assertEquals(4, SettingsManager.difficulty)
    }

    @Test
    fun `test initial default statistics`() {
        // Проверяем начальную статистику
        assertEquals(0, SettingsManager.gamesPlayed)
        assertEquals(Int.MAX_VALUE, SettingsManager.bestTime)
        assertEquals(0, SettingsManager.totalMatches)
        assertTrue(SettingsManager.achievements.isEmpty())
    }

    @Test
    fun `test toggle animations`() {
        // Тестируем переключение анимаций
        assertTrue(SettingsManager.animationsEnabled)
        
        SettingsManager.toggleAnimations()
        assertFalse(SettingsManager.animationsEnabled)
        
        SettingsManager.toggleAnimations()
        assertTrue(SettingsManager.animationsEnabled)
    }

    @Test
    fun `test toggle sound`() {
        // Тестируем переключение звука
        assertTrue(SettingsManager.soundEnabled)
        
        SettingsManager.toggleSound()
        assertFalse(SettingsManager.soundEnabled)
        
        SettingsManager.toggleSound()
        assertTrue(SettingsManager.soundEnabled)
    }

    @Test
    fun `test set valid theme`() {
        // Тестируем установку валидных тем
        SettingsManager.setTheme("light")
        assertEquals("light", SettingsManager.currentTheme)
        
        SettingsManager.setTheme("dark")
        assertEquals("dark", SettingsManager.currentTheme)
    }

    @Test
    fun `test set invalid theme`() {
        // Тестируем установку невалидной темы
        val originalTheme = SettingsManager.currentTheme
        SettingsManager.setTheme("invalid")
        assertEquals(originalTheme, SettingsManager.currentTheme)
    }

    @Test
    fun `test set valid difficulty`() {
        // Тестируем установку валидных уровней сложности
        SettingsManager.setDifficulty(6)
        assertEquals(6, SettingsManager.difficulty)
        
        SettingsManager.setDifficulty(8)
        assertEquals(8, SettingsManager.difficulty)
        
        SettingsManager.setDifficulty(4)
        assertEquals(4, SettingsManager.difficulty)
    }

    @Test
    fun `test set invalid difficulty`() {
        // Тестируем установку невалидного уровня сложности
        val originalDifficulty = SettingsManager.difficulty
        SettingsManager.setDifficulty(5)
        assertEquals(originalDifficulty, SettingsManager.difficulty)
        
        SettingsManager.setDifficulty(10)
        assertEquals(originalDifficulty, SettingsManager.difficulty)
    }

    @Test
    fun `test update game stats first game`() {
        // Тестируем обновление статистики первой игры
        SettingsManager.updateGameStats(45, 8)
        
        assertEquals(1, SettingsManager.gamesPlayed)
        assertEquals(45, SettingsManager.bestTime)
        assertEquals(8, SettingsManager.totalMatches)
        assertTrue(SettingsManager.achievements.contains("🎮 Первая игра!"))
    }

    @Test
    fun `test update game stats multiple games`() {
        // Тестируем обновление статистики нескольких игр
        SettingsManager.updateGameStats(60, 8)
        SettingsManager.updateGameStats(30, 6)
        SettingsManager.updateGameStats(90, 10)
        
        assertEquals(3, SettingsManager.gamesPlayed)
        assertEquals(30, SettingsManager.bestTime) // Лучшее время
        assertEquals(24, SettingsManager.totalMatches) // Общее количество совпадений
    }

    @Test
    fun `test achievements milestones`() {
        // Тестируем достижения по количеству игр
        repeat(10) { SettingsManager.updateGameStats(60, 8) }
        assertTrue(SettingsManager.achievements.contains("🔥 10 игр сыграно!"))
        
        repeat(40) { SettingsManager.updateGameStats(60, 8) }
        assertTrue(SettingsManager.achievements.contains("💎 50 игр сыграно!"))
        
        repeat(50) { SettingsManager.updateGameStats(60, 8) }
        assertTrue(SettingsManager.achievements.contains("👑 100 игр сыграно!"))
    }

    @Test
    fun `test time achievements`() {
        // Сбрасываем статистику для чистого теста
        SettingsManager.resetStats()
        
        // Тестируем достижения по времени
        SettingsManager.updateGameStats(25, 8)
        assertTrue(SettingsManager.achievements.contains("⚡ Молниеносная победа!"), 
            "Should have lightning victory achievement for time <= 30")
        
        // Сбрасываем и тестируем второе достижение
        SettingsManager.resetStats()
        SettingsManager.updateGameStats(45, 8)
        assertTrue(SettingsManager.achievements.contains("🚀 Быстрая победа!"), 
            "Should have fast victory achievement for time <= 60")
    }

    @Test
    fun `test matches achievements`() {
        // Сбрасываем статистику для чистого теста
        SettingsManager.resetStats()
        
        // Тестируем достижения по количеству совпадений
        // Нужно накопить 100+ совпадений
        repeat(13) { SettingsManager.updateGameStats(60, 8) } // 13 * 8 = 104 совпадения
        assertTrue(SettingsManager.achievements.contains("💯 100 совпадений!"), 
            "Should have 100 matches achievement")
        
        // Сбрасываем и тестируем 500+ совпадений
        SettingsManager.resetStats()
        repeat(63) { SettingsManager.updateGameStats(60, 8) } // 63 * 8 = 504 совпадения
        assertTrue(SettingsManager.achievements.contains("🎊 500 совпадений!"), 
            "Should have 500 matches achievement")
    }

    @Test
    fun `test add achievement manually`() {
        // Тестируем ручное добавление достижения
        SettingsManager.addAchievement("🏆 Кастомное достижение!")
        assertTrue(SettingsManager.achievements.contains("🏆 Кастомное достижение!"))
        
        // Проверяем, что достижение не дублируется
        SettingsManager.addAchievement("🏆 Кастомное достижение!")
        assertEquals(1, SettingsManager.achievements.size)
    }

    @Test
    fun `test reset stats`() {
        // Тестируем сброс статистики
        SettingsManager.updateGameStats(45, 8)
        SettingsManager.addAchievement("🏆 Тест")
        
        SettingsManager.resetStats()
        
        assertEquals(0, SettingsManager.gamesPlayed)
        assertEquals(Int.MAX_VALUE, SettingsManager.bestTime)
        assertEquals(0, SettingsManager.totalMatches)
        assertTrue(SettingsManager.achievements.isEmpty())
    }

    @Test
    fun `test reset to defaults`() {
        // Тестируем сброс настроек к значениям по умолчанию
        SettingsManager.toggleAnimations()
        SettingsManager.toggleSound()
        SettingsManager.setTheme("light")
        SettingsManager.setDifficulty(8)
        
        SettingsManager.resetToDefaults()
        
        assertTrue(SettingsManager.animationsEnabled)
        assertTrue(SettingsManager.soundEnabled)
        assertEquals("dark", SettingsManager.currentTheme)
        assertEquals(4, SettingsManager.difficulty)
    }

    @Test
    fun `test get settings string`() {
        // Тестируем получение строкового представления настроек
        SettingsManager.updateGameStats(45, 8)
        
        val settingsString = SettingsManager.getSettingsString()
        
        // Проверяем основные компоненты строки настроек
        assertTrue(settingsString.contains("Animations: ON"))
        assertTrue(settingsString.contains("Sound: ON"))
        assertTrue(settingsString.contains("Theme: dark"))
        assertTrue(settingsString.contains("Difficulty: 4x4"))
        assertTrue(settingsString.contains("Games played: 1"))
        assertTrue(settingsString.contains("Best time: 00:45"))
        assertTrue(settingsString.contains("Total matches: 8"))
        // Проверяем, что строка содержит информацию о достижениях (количество может варьироваться)
        assertTrue(settingsString.contains("Achievements:"))
    }

    @Test
    fun `test get settings string with no games played`() {
        // Тестируем строковое представление без сыгранных игр
        val settingsString = SettingsManager.getSettingsString()
        
        assertTrue(settingsString.contains("Best time: None"))
        assertTrue(settingsString.contains("Games played: 0"))
    }

    @Test
    fun `test singleton behavior`() {
        // Тестируем поведение Singleton
        val instance1 = SettingsManager
        val instance2 = SettingsManager
        
        assertSame(instance1, instance2)
        
        // Изменения в одном экземпляре должны отражаться в другом
        instance1.toggleAnimations()
        assertFalse(instance2.animationsEnabled)
    }

    @Test
    fun `test achievements immutable copy`() {
        // Тестируем, что achievements возвращает неизменяемую копию
        SettingsManager.addAchievement("🏆 Тест")
        val achievements = SettingsManager.achievements
        
        // Попытка изменить возвращенный Set не должна влиять на оригинал
        assertThrows(UnsupportedOperationException::class.java) {
            (achievements as MutableSet<String>).add("🏆 Другой тест")
        }
        
        assertEquals(1, SettingsManager.achievements.size)
    }

    @Test
    fun `test complex scenario`() {
        // Комплексный тест сценария игры
        SettingsManager.setTheme("light")
        SettingsManager.setDifficulty(6)
        SettingsManager.toggleSound()
        
        // Играем несколько игр
        SettingsManager.updateGameStats(120, 18) // Первая игра
        SettingsManager.updateGameStats(90, 18)  // Вторая игра
        SettingsManager.updateGameStats(45, 18)  // Третья игра (лучшее время)
        
        // Проверяем финальное состояние
        assertEquals(3, SettingsManager.gamesPlayed)
        assertEquals(45, SettingsManager.bestTime)
        assertEquals(54, SettingsManager.totalMatches)
        assertTrue(SettingsManager.achievements.contains("🎮 Первая игра!"))
        assertTrue(SettingsManager.achievements.contains("🚀 Быстрая победа!"))
        
        // Проверяем настройки
        assertFalse(SettingsManager.soundEnabled)
        assertEquals("light", SettingsManager.currentTheme)
        assertEquals(6, SettingsManager.difficulty)
    }
}
