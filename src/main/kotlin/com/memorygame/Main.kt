package com.memorygame

import javax.swing.SwingUtilities
import javax.swing.UIManager

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

