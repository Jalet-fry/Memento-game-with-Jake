package com.memorygame

import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.ActionListener
import javax.swing.*
import java.io.File
import java.net.URL

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
    private lateinit var animationPanel: JPanel
    private lateinit var controlPanel: JPanel // Новая панель управления
    private lateinit var infoPanel: JPanel
    private lateinit var buttonPanel: JPanel
    
    private var timerJob: Job? = null
    private var currentAnimationLabel: JLabel? = null
    
    // Новые переменные для улучшений
    private var animationsEnabled = true
    private var soundEnabled = true
    private var currentTheme = "dark"
    private var difficulty = 4 // 4x4 по умолчанию
    private var gamesPlayed = 0
    private var bestTime = Int.MAX_VALUE
    private var totalMatches = 0
    private var achievements = mutableSetOf<String>()
    
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
    
    // Пути к анимациям
    private val animationPaths = listOf(
        "/animations/001.gif",
        "/animations/002.gif",
        "/animations/003.gif",
        "/animations/004.gif",
        "/animations/005.gif",
        "/animations/006.gif",
        "/animations/007.gif",
        "/animations/008.gif",
        "/animations/009.gif",
        "/animations/010.gif",
        "/animations/011.gif",
        "/animations/012.gif",
        "/animations/013.gif",
        "/animations/014.gif",
        "/animations/015.gif",
        "/animations/016.gif",
        "/animations/017.gif",
        "/animations/018.gif",
        "/animations/019.gif",
        "/animations/020.gif"
    )
    
    // Специальные анимации для событий
    private val specialAnimations = mapOf(
        "match" to "/animations/Jake laugh.gif",
        "win" to "/animations/Jake dance.gif",
        "miss" to "/animations/Jake vig eyes.gif",
        "start" to "/animations/Jake.gif"
    )
    
    init {
        setupUI()
        initGame()
        startTimer()
        showAnimation("start")
    }
    
    private fun setupUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout(10, 10)
        
        // Панель информации сверху
        infoPanel = JPanel(GridLayout(1, 3, 10, 0))
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
        
                // Панель анимации (увеличена)
                animationPanel = JPanel(BorderLayout())
                animationPanel.background = Color(30, 30, 40)
                animationPanel.preferredSize = Dimension(300, 400) // Увеличено с 200x200 до 300x400
                animationPanel.border = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color(100, 100, 100), 2),
                    "🎬 Анимация Jake",
                    javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.TOP,
                    Font("Arial", Font.BOLD, 16),
                    Color(255, 215, 0) // Золотой цвет
                )
        
        // Игровая панель
        gamePanel = JPanel(GridLayout(gridSize, gridSize, 5, 5))
        gamePanel.background = Color(30, 30, 40)
        gamePanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        
        // Панель кнопок снизу
        buttonPanel = JPanel(FlowLayout())
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
        
        // Создаем панель управления
        controlPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 5))
        controlPanel.background = Color(40, 40, 50)
        
        val animToggleButton = JButton("🎬 Анимации: ВКЛ").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                animationsEnabled = !animationsEnabled
                text = if (animationsEnabled) "🎬 Анимации: ВКЛ" else "🎬 Анимации: ВЫКЛ"
                if (!animationsEnabled) clearAnimation()
            }
        }
        
        val soundToggleButton = JButton("🔊 Звук: ВКЛ").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                soundEnabled = !soundEnabled
                text = if (soundEnabled) "🔊 Звук: ВКЛ" else "🔊 Звук: ВЫКЛ"
            }
        }
        
        val difficultyButton = JButton("📊 Сложность: 4x4").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                val options = arrayOf("4x4 (Легко)", "6x6 (Средне)", "8x8 (Сложно)")
                val choice = JOptionPane.showOptionDialog(
                    this@MemoryGame,
                    "Выберите уровень сложности:",
                    "Сложность игры",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                )
                when (choice) {
                    0 -> { difficulty = 4; text = "📊 Сложность: 4x4" }
                    1 -> { difficulty = 6; text = "📊 Сложность: 6x6" }
                    2 -> { difficulty = 8; text = "📊 Сложность: 8x8" }
                }
                if (choice != JOptionPane.CLOSED_OPTION) {
                    resetGame()
                }
            }
        }
        
        val statsButton = JButton("📈 Статистика").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener { showStatistics() }
        }
        
        val themeButton = JButton("🎨 Тема: Темная").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                currentTheme = if (currentTheme == "dark") "light" else "dark"
                text = if (currentTheme == "dark") "🎨 Тема: Темная" else "🎨 Тема: Светлая"
                applyTheme()
            }
        }
        
        controlPanel.add(animToggleButton)
        controlPanel.add(soundToggleButton)
        controlPanel.add(difficultyButton)
        controlPanel.add(themeButton)
        controlPanel.add(statsButton)
        
        buttonPanel.add(newGameButton)
        buttonPanel.add(exitButton)
        
        // Создаем центральную панель с игрой и анимацией
        val centerPanel = JPanel(BorderLayout(10, 0))
        centerPanel.background = Color(30, 30, 40)
        centerPanel.add(gamePanel, BorderLayout.CENTER)
        centerPanel.add(animationPanel, BorderLayout.EAST)
        
        // Создаем главную панель с вертикальным расположением
        val mainPanel = JPanel(BorderLayout())
        mainPanel.background = Color(30, 30, 40)
        
        mainPanel.add(infoPanel, BorderLayout.NORTH)
        mainPanel.add(controlPanel, BorderLayout.CENTER)
        mainPanel.add(centerPanel, BorderLayout.CENTER)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        
        add(mainPanel)
        
        setSize(1200, 900) // Еще больше для всех новых панелей
        setLocationRelativeTo(null)
        background = Color(30, 30, 40)
    }
    
    private fun initGame() {
        cards.clear()
        gamePanel.removeAll()
        
        // Обновляем размер сетки в зависимости от сложности
        val currentGridSize = difficulty
        val currentTotalPairs = (currentGridSize * currentGridSize) / 2
        
        // Обновляем layout панели игры
        gamePanel.layout = GridLayout(currentGridSize, currentGridSize, 5, 5)
        
        // Создаем список пар карточек
        val cardPairs = mutableListOf<Pair<Int, String>>()
        for (i in 0 until currentTotalPairs) {
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
        
        matchedPairs = 0
        attempts = 0
        attemptsLabel.text = "Попытки: $attempts"
        statusLabel.text = "Найдите пары! Сложность: ${currentGridSize}x${currentGridSize}"
        statusLabel.foreground = Color(100, 200, 100)
        
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
                playSound("flip")
            }
            secondCard == null && card != firstCard -> {
                secondCard = card
                card.flip()
                playSound("flip")
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
            showAnimation("match")
            playSound("match")
            showParticleEffect(first, second) // Добавляем эффект частиц
            SwingUtilities.invokeLater {
                first.setMatched()
                second.setMatched()
                matchedPairs++
                statusLabel.text = "✨ Совпадение! ($matchedPairs/$totalPairs) ✨"
                statusLabel.foreground = Color.GREEN
                
                firstCard = null
                secondCard = null
                isBusy = false
                
                checkWin()
            }
        } else {
            // Не совпали
            showAnimation("miss")
            playSound("miss")
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
            showAnimation("win")
            playSound("win")
            statusLabel.text = "🎉 ПОБЕДА! 🎉"
            statusLabel.foreground = Color.YELLOW
            
            // Обновляем статистику
            gamesPlayed++
            totalMatches += matchedPairs
            if (elapsedSeconds < bestTime) {
                bestTime = elapsedSeconds
                achievements.add("🏆 Рекорд времени!")
            }
            
            // Проверяем достижения
            checkAchievements()
            
            val message = """
                🎉 Поздравляем! Вы выиграли! 🎉
                
                ⏱️ Время: ${formatTime(elapsedSeconds)}
                🎯 Попытки: $attempts
                📊 Сложность: ${difficulty}x${difficulty}
                
                🏆 Лучшее время: ${if (bestTime == Int.MAX_VALUE) "Нет" else formatTime(bestTime)}
                🎮 Игр сыграно: $gamesPlayed
                
                ${if (achievements.isNotEmpty()) "🏅 Достижения: ${achievements.joinToString(", ")}" else ""}
                
                Хотите сыграть еще раз?
            """.trimIndent()
            
            val result = JOptionPane.showConfirmDialog(
                this,
                message,
                "🎉 Победа! 🎉",
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
    
    /**
     * Показывает анимацию по типу события
     */
    private fun showAnimation(eventType: String) {
        if (!animationsEnabled) return
        
        println("Запрос анимации для события: $eventType")
        val animationPath = specialAnimations[eventType] ?: animationPaths.random()
        println("Выбранный путь анимации: $animationPath")
        showGifAnimation(animationPath)
    }
    
    /**
     * Показывает случайную анимацию
     */
    private fun showRandomAnimation() {
        val randomPath = animationPaths.random()
        showGifAnimation(randomPath)
    }
    
    /**
     * Показывает GIF анимацию
     */
    private fun showGifAnimation(animationPath: String) {
        SwingUtilities.invokeLater {
            try {
                println("Попытка загрузить анимацию: $animationPath")
                
                // Удаляем предыдущую анимацию
                currentAnimationLabel?.let { animationPanel.remove(it) }
                
                // Загружаем новую анимацию
                val resourceUrl = javaClass.getResource(animationPath)
                if (resourceUrl != null) {
                    println("Ресурс найден: $resourceUrl")
                    val imageIcon = ImageIcon(resourceUrl)
                    if (imageIcon.iconWidth > 0) {
                        println("Анимация загружена успешно: ${imageIcon.iconWidth}x${imageIcon.iconHeight}")
                        currentAnimationLabel = JLabel(imageIcon).apply {
                            horizontalAlignment = SwingConstants.CENTER
                            verticalAlignment = SwingConstants.CENTER
                        }
                        animationPanel.add(currentAnimationLabel, BorderLayout.CENTER)
                        animationPanel.revalidate()
                        animationPanel.repaint()
                    } else {
                        println("Анимация не загрузилась (размер 0)")
                        showRandomAnimation()
                    }
                } else {
                    println("Ресурс не найден: $animationPath")
                    // Показываем случайную анимацию в случае ошибки
                    showRandomAnimation()
                }
            } catch (e: Exception) {
                println("Ошибка загрузки анимации: $animationPath - ${e.message}")
                e.printStackTrace()
                // Показываем случайную анимацию в случае ошибки
                showRandomAnimation()
            }
        }
    }
    
    /**
     * Очищает панель анимации
     */
    private fun clearAnimation() {
        SwingUtilities.invokeLater {
            currentAnimationLabel?.let { animationPanel.remove(it) }
            currentAnimationLabel = null
            animationPanel.revalidate()
            animationPanel.repaint()
        }
    }
    
    /**
     * Показывает статистику игрока
     */
    private fun showStatistics() {
        val avgTime = if (gamesPlayed > 0) (bestTime.toDouble() / gamesPlayed) else 0.0
        val avgAttempts = if (gamesPlayed > 0) (totalMatches.toDouble() / gamesPlayed) else 0.0
        
        val message = """
            📊 СТАТИСТИКА ИГРЫ 📊
            
            🎮 Всего игр: $gamesPlayed
            🏆 Лучшее время: ${if (bestTime == Int.MAX_VALUE) "Нет" else formatTime(bestTime)}
            ⏱️ Среднее время: ${String.format("%.1f", avgTime)} сек
            🎯 Всего совпадений: $totalMatches
            📈 Среднее совпадений за игру: ${String.format("%.1f", avgAttempts)}
            
            🏅 Достижения (${achievements.size}):
            ${if (achievements.isEmpty()) "Пока нет достижений" else achievements.joinToString("\n")}
            
            🎨 Текущие настройки:
            • Анимации: ${if (animationsEnabled) "Включены" else "Выключены"}
            • Звук: ${if (soundEnabled) "Включен" else "Выключен"}
            • Сложность: ${difficulty}x${difficulty}
        """.trimIndent()
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "📊 Статистика",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
    
    /**
     * Проверяет и добавляет достижения
     */
    private fun checkAchievements() {
        when {
            gamesPlayed == 1 -> achievements.add("🎮 Первая игра!")
            gamesPlayed == 10 -> achievements.add("🔥 10 игр сыграно!")
            gamesPlayed == 50 -> achievements.add("💎 50 игр сыграно!")
            gamesPlayed == 100 -> achievements.add("👑 100 игр сыграно!")
            attempts <= 8 && difficulty == 4 -> achievements.add("🎯 Мастер 4x4!")
            attempts <= 18 && difficulty == 6 -> achievements.add("🎯 Мастер 6x6!")
            attempts <= 32 && difficulty == 8 -> achievements.add("🎯 Мастер 8x8!")
            elapsedSeconds <= 30 -> achievements.add("⚡ Молниеносная победа!")
            elapsedSeconds <= 60 -> achievements.add("🚀 Быстрая победа!")
            totalMatches >= 100 -> achievements.add("💯 100 совпадений!")
            totalMatches >= 500 -> achievements.add("🎊 500 совпадений!")
        }
    }
    
    /**
     * Воспроизводит звуковой эффект (заглушка)
     */
    private fun playSound(soundType: String) {
        if (!soundEnabled) return
        
        // Здесь можно добавить реальные звуковые эффекты
        when (soundType) {
            "match" -> println("🔊 Звук совпадения")
            "miss" -> println("🔊 Звук промаха")
            "win" -> println("🔊 Звук победы")
            "flip" -> println("🔊 Звук переворота карты")
        }
    }
    
    /**
     * Показывает эффект частиц при совпадении карт
     */
    private fun showParticleEffect(card1: MemoryCard, card2: MemoryCard) {
        if (!animationsEnabled) return
        
        SwingUtilities.invokeLater {
            // Создаем временные метки с эмодзи для эффекта частиц
            val particleLabels = mutableListOf<JLabel>()
            val colors = arrayOf(Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN, Color.MAGENTA)
            val emojis = arrayOf("✨", "⭐", "💫", "🌟", "💎")
            
            // Получаем позиции карт
            val card1Bounds = card1.bounds
            val card2Bounds = card2.bounds
            
            // Создаем 10 частиц
            repeat(10) { i ->
                val particle = JLabel(emojis[i % emojis.size]).apply {
                    font = Font("Arial", Font.BOLD, 16)
                    foreground = colors[i % colors.size]
                    size = Dimension(20, 20)
                    
                    // Позиционируем частицы между картами
                    val centerX = (card1Bounds.x + card1Bounds.width/2 + card2Bounds.x + card2Bounds.width/2) / 2
                    val centerY = (card1Bounds.y + card1Bounds.height/2 + card2Bounds.y + card2Bounds.height/2) / 2
                    
                    location = Point(
                        centerX + (Math.random() * 60 - 30).toInt(),
                        centerY + (Math.random() * 60 - 30).toInt()
                    )
                }
                
                gamePanel.add(particle)
                particleLabels.add(particle)
                gamePanel.revalidate()
                gamePanel.repaint()
            }
            
            // Анимация исчезновения частиц
            Timer(50) { timer ->
                particleLabels.forEach { particle ->
                    val currentLocation = particle.location
                    particle.location = Point(
                        currentLocation.x + (Math.random() * 4 - 2).toInt(),
                        currentLocation.y - 2
                    )
                    particle.foreground = Color(
                        particle.foreground.red,
                        particle.foreground.green,
                        particle.foreground.blue,
                        maxOf(0, particle.foreground.alpha - 25)
                    )
                }
                gamePanel.repaint()
            }.apply {
                isRepeats = true
                start()
            }
            
            // Удаляем частицы через 2 секунды
            Timer(2000) { _ ->
                particleLabels.forEach { particle ->
                    gamePanel.remove(particle)
                }
                gamePanel.revalidate()
                gamePanel.repaint()
            }.apply {
                isRepeats = false
                start()
            }
        }
    }
    
    /**
     * Применяет выбранную тему оформления
     */
    private fun applyTheme() {
        when (currentTheme) {
            "dark" -> {
                // Темная тема (текущая)
                background = Color(30, 30, 40)
                infoPanel.background = Color(40, 40, 50)
                controlPanel.background = Color(40, 40, 50)
                gamePanel.background = Color(30, 30, 40)
                animationPanel.background = Color(30, 30, 40)
                buttonPanel.background = Color(40, 40, 50)
                
                timerLabel.foreground = Color.WHITE
                attemptsLabel.foreground = Color.WHITE
                statusLabel.foreground = Color(100, 200, 100)
            }
            "light" -> {
                // Светлая тема
                background = Color(240, 240, 240)
                infoPanel.background = Color(220, 220, 220)
                controlPanel.background = Color(220, 220, 220)
                gamePanel.background = Color(250, 250, 250)
                animationPanel.background = Color(250, 250, 250)
                buttonPanel.background = Color(220, 220, 220)
                
                timerLabel.foreground = Color.BLACK
                attemptsLabel.foreground = Color.BLACK
                statusLabel.foreground = Color(0, 100, 0)
            }
        }
        
        // Обновляем границы панели анимации
        animationPanel.border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(
                if (currentTheme == "dark") Color(100, 100, 100) else Color(150, 150, 150), 
                2
            ),
            "🎬 Анимация Jake",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            Font("Arial", Font.BOLD, 16),
            if (currentTheme == "dark") Color(255, 215, 0) else Color(200, 100, 0)
        )
        
        repaint()
    }
}

