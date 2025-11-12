package com.memorygame

import com.memorygame.backend.StatisticsManager
import com.memorygame.ui.MemoryGame
import com.memorygame.ui.PlayerSelectionDialog
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Точка входа в приложение "Игра Мементо с Jake"
 * 
 * Инициализирует пользовательский интерфейс и запускает игру.
 * Использует системный Look and Feel для лучшего внешнего вида.
 * Инициализирует StatisticsManager для управления статистикой игроков.
 */
fun main() {
    // Устанавливаем системный look and feel для лучшего вида
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    // Инициализируем StatisticsManager
    if (!StatisticsManager.initialize()) {
        println("Предупреждение: Не удалось инициализировать StatisticsManager")
    }
    
    // Запускаем игру в потоке GUI
    // Используем invokeAndWait для синхронного выполнения, чтобы main() не завершился до показа диалога
    try {
        SwingUtilities.invokeAndWait {
            // Всегда показываем диалог выбора игрока при запуске
            // Это позволяет пользователю выбрать или создать игрока
            val game = MemoryGame()
            game.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            game.isVisible = false // Скрываем окно до выбора игрока
            
            val dialog = PlayerSelectionDialog(game)
            val selectedPlayer = dialog.showDialog()
            
            // Определяем, какого игрока использовать для игры
            val playerToUse = when {
                // Приоритет 1: Явно выбранный игрок из диалога (созданный или выбранный)
                selectedPlayer != null -> selectedPlayer
                // Приоритет 2: Текущий игрок из StatisticsManager (если диалог закрыт без выбора)
                else -> StatisticsManager.getCurrentPlayer()
            }
            
            if (playerToUse != null) {
                // Явно устанавливаем выбранного игрока как текущего перед показом игры
                // Это гарантирует, что игра будет работать за правильного игрока
                StatisticsManager.setCurrentPlayer(playerToUse.name)
                game.isVisible = true
            } else {
                // Игрок не выбран и текущего игрока нет - закрываем приложение
                System.exit(0)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        System.exit(1)
    }
}

