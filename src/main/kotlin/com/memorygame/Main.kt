package com.memorygame

import com.memorygame.ui.MemoryGame
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Точка входа в приложение "Игра Мементо с Jake"
 * 
 * Инициализирует пользовательский интерфейс и запускает игру.
 * Использует системный Look and Feel для лучшего внешнего вида.
 */
fun main() {
    // Устанавливаем системный look and feel для лучшего вида
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    // Запускаем игру в потоке GUI
    SwingUtilities.invokeLater {
        val game = MemoryGame()
        game.isVisible = true
    }
}

