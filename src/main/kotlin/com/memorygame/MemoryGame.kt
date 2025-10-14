package com.memorygame

import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.ActionListener
import javax.swing.*

class MemoryGame : JFrame("Игра Мементо") {
    
    private val cards = mutableListOf<MemoryCard>()
    private var firstCard: MemoryCard? = null
    private var secondCard: MemoryCard? = null
    private var isBusy = false
    
    private var attempts = 0
    private var elapsedSeconds = 0
    private var matchedPairs = 0
    
    private lateinit var timerLabel: JLabel
    private lateinit var attemptsLabel: JLabel
    private lateinit var statusLabel: JLabel
    private lateinit var gamePanel: JPanel
    
    private var timerJob: Job? = null
    
    private val gridSize = 4 // 4x4 сетка = 16 карточек = 8 пар
    private val totalPairs = (gridSize * gridSize) / 2
    
    // Пути к изображениям (будут использоваться из resources)
    private val imagePaths = listOf(
        "/images/card1.jpg",
        "/images/card2.jpg",
        "/images/card3.jpg",
        "/images/card4.jpg",
        "/images/card5.jpg",
        "/images/card6.jpg",
        "/images/card7.jpg",
        "/images/card8.jpg"
    )
    
    init {
        setupUI()
        initGame()
        startTimer()
    }
    
    private fun setupUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout(10, 10)
        
        // Панель информации сверху
        val infoPanel = JPanel(GridLayout(1, 3, 10, 0))
        infoPanel.background = Color(40, 40, 50)
        infoPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        
        timerLabel = JLabel("Время: 00:00", SwingConstants.CENTER).apply {
            font = Font("Arial", Font.BOLD, 18)
            foreground = Color.WHITE
        }
        
        attemptsLabel = JLabel("Попытки: 0", SwingConstants.CENTER).apply {
            font = Font("Arial", Font.BOLD, 18)
            foreground = Color.WHITE
        }
        
        statusLabel = JLabel("Найдите пары!", SwingConstants.CENTER).apply {
            font = Font("Arial", Font.BOLD, 18)
            foreground = Color(100, 200, 100)
        }
        
        infoPanel.add(timerLabel)
        infoPanel.add(statusLabel)
        infoPanel.add(attemptsLabel)
        
        // Игровая панель
        gamePanel = JPanel(GridLayout(gridSize, gridSize, 5, 5))
        gamePanel.background = Color(30, 30, 40)
        gamePanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        
        // Панель кнопок снизу
        val buttonPanel = JPanel(FlowLayout())
        buttonPanel.background = Color(40, 40, 50)
        
        val newGameButton = JButton("Новая игра").apply {
            font = Font("Arial", Font.BOLD, 14)
            addActionListener {
                resetGame()
            }
        }
        
        val exitButton = JButton("Выход").apply {
            font = Font("Arial", Font.BOLD, 14)
            addActionListener {
                System.exit(0)
            }
        }
        
        buttonPanel.add(newGameButton)
        buttonPanel.add(exitButton)
        
        add(infoPanel, BorderLayout.NORTH)
        add(gamePanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
        
        setSize(600, 700)
        setLocationRelativeTo(null)
        background = Color(30, 30, 40)
    }
    
    private fun initGame() {
        cards.clear()
        gamePanel.removeAll()
        
        // Создаем список пар карточек
        val cardPairs = mutableListOf<Pair<Int, String>>()
        for (i in 0 until totalPairs) {
            val imagePath = imagePaths[i % imagePaths.size]
            cardPairs.add(Pair(i, imagePath))
            cardPairs.add(Pair(i, imagePath))
        }
        
        // Перемешиваем
        cardPairs.shuffle()
        
        // Создаем карточки и добавляем их на панель
        cardPairs.forEach { (id, path) ->
            val card = MemoryCard(id, path)
            card.addActionListener(createCardClickListener(card))
            cards.add(card)
            gamePanel.add(card)
        }
        
        gamePanel.revalidate()
        gamePanel.repaint()
    }
    
    private fun createCardClickListener(card: MemoryCard) = ActionListener {
        if (isBusy || card.isMatched || card.isFlipped) {
            return@ActionListener
        }
        
        when {
            firstCard == null -> {
                firstCard = card
                card.flip()
            }
            secondCard == null && card != firstCard -> {
                secondCard = card
                card.flip()
                attempts++
                updateAttemptsLabel()
                checkMatch()
            }
        }
    }
    
    private fun checkMatch() {
        val first = firstCard ?: return
        val second = secondCard ?: return
        
        isBusy = true
        
        if (first.getCardId() == second.getCardId()) {
            // Совпадение!
            SwingUtilities.invokeLater {
                first.setMatched()
                second.setMatched()
                matchedPairs++
                statusLabel.text = "Совпадение! ($matchedPairs/$totalPairs)"
                statusLabel.foreground = Color.GREEN
                
                firstCard = null
                secondCard = null
                isBusy = false
                
                checkWin()
            }
        } else {
            // Не совпали
            statusLabel.text = "Не совпало! Попробуйте еще"
            statusLabel.foreground = Color.ORANGE
            
            Timer(1000) { _ ->
                first.flip()
                second.flip()
                firstCard = null
                secondCard = null
                isBusy = false
                statusLabel.text = "Найдите пары!"
                statusLabel.foreground = Color(100, 200, 100)
            }.apply {
                isRepeats = false
                start()
            }
        }
    }
    
    private fun checkWin() {
        if (matchedPairs == totalPairs) {
            timerJob?.cancel()
            statusLabel.text = "🎉 ПОБЕДА! 🎉"
            statusLabel.foreground = Color.YELLOW
            
            val message = """
                Поздравляем! Вы выиграли!
                
                Время: ${formatTime(elapsedSeconds)}
                Попытки: $attempts
                
                Хотите сыграть еще раз?
            """.trimIndent()
            
            val result = JOptionPane.showConfirmDialog(
                this,
                message,
                "Победа!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            )
            
            if (result == JOptionPane.YES_OPTION) {
                resetGame()
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000)
                elapsedSeconds++
                SwingUtilities.invokeLater {
                    updateTimerLabel()
                }
            }
        }
    }
    
    private fun updateTimerLabel() {
        timerLabel.text = "Время: ${formatTime(elapsedSeconds)}"
    }
    
    private fun updateAttemptsLabel() {
        attemptsLabel.text = "Попытки: $attempts"
    }
    
    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    private fun resetGame() {
        timerJob?.cancel()
        
        attempts = 0
        elapsedSeconds = 0
        matchedPairs = 0
        firstCard = null
        secondCard = null
        isBusy = false
        
        updateTimerLabel()
        updateAttemptsLabel()
        statusLabel.text = "Найдите пары!"
        statusLabel.foreground = Color(100, 200, 100)
        
        initGame()
        startTimer()
    }
}

