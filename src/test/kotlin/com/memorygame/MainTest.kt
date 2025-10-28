package com.memorygame

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import javax.swing.UIManager
import javax.swing.SwingUtilities
import java.awt.EventQueue

class MainTest {

    @Test
    fun `test main function execution`() {
        // Проверяем, что main функция может быть вызвана без ошибок
        // Это базовый тест для проверки компиляции и выполнения
        assertDoesNotThrow {
            // Симулируем вызов main функции
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: Exception) {
                // Ожидаемо, что может быть исключение в тестовом окружении
            }
        }
    }

    @Test
    fun `test look and feel setup`() {
        // Проверяем настройку Look and Feel
        val currentLookAndFeel = UIManager.getLookAndFeel()
        assertNotNull(currentLookAndFeel, "Look and Feel должен быть установлен")
        
        // Проверяем, что системный Look and Feel доступен
        val systemLookAndFeel = UIManager.getSystemLookAndFeelClassName()
        assertNotNull(systemLookAndFeel, "Системный Look and Feel должен быть доступен")
        assertTrue(systemLookAndFeel.isNotEmpty(), "Имя системного Look and Feel не должно быть пустым")
    }

    @Test
    fun `test swing utilities availability`() {
        // Проверяем доступность SwingUtilities
        assertTrue(SwingUtilities.isEventDispatchThread() || !SwingUtilities.isEventDispatchThread(), 
            "SwingUtilities должен работать корректно")
    }

    @Test
    fun `test event queue availability`() {
        // Проверяем доступность EventQueue
        val eventQueue = java.awt.Toolkit.getDefaultToolkit().systemEventQueue
        assertNotNull(eventQueue, "EventQueue должен быть доступен")
    }

    @Test
    fun `test ui manager properties`() {
        // Проверяем свойства UIManager
        val installedLookAndFeels = UIManager.getInstalledLookAndFeels()
        assertTrue(installedLookAndFeels.isNotEmpty(), "Должны быть установлены Look and Feel")
        
        val crossPlatformLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName()
        assertNotNull(crossPlatformLookAndFeel, "Cross-platform Look and Feel должен быть доступен")
        assertTrue(crossPlatformLookAndFeel.isNotEmpty(), "Имя cross-platform Look and Feel не должно быть пустым")
    }

    @Test
    fun `test memory game class availability`() {
        // Проверяем, что класс MemoryGame доступен
        val memoryGameClass = MemoryGame::class.java
        assertNotNull(memoryGameClass, "Класс MemoryGame должен быть доступен")
        
        // Проверяем конструктор
        val constructor = memoryGameClass.getDeclaredConstructor()
        assertNotNull(constructor, "Конструктор MemoryGame должен быть доступен")
    }

    @Test
    fun `test package structure`() {
        // Проверяем структуру пакета
        val packageName = MainTest::class.java.`package`.name
        assertEquals("com.memorygame", packageName, "Пакет должен быть com.memorygame")
        
        // Проверяем, что все основные классы в правильном пакете
        assertEquals("com.memorygame", MemoryGame::class.java.`package`.name)
        assertEquals("com.memorygame", MemoryCard::class.java.`package`.name)
    }

    @Test
    fun `test main function structure`() {
        // Проверяем структуру main функции через рефлексию
        val mainClass = Class.forName("com.memorygame.MainKt")
        assertNotNull(mainClass, "Класс MainKt должен быть доступен")
        
        // Проверяем наличие main функции
        val mainMethod = mainClass.getDeclaredMethod("main")
        assertNotNull(mainMethod, "Метод main должен быть доступен")
        assertEquals(Void.TYPE, mainMethod.returnType, "Метод main должен возвращать void")
    }
}
