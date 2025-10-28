package com.memorygame

import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.ActionListener
import javax.swing.*

/**
 * MemoryGame - основная игра с применением паттернов проектирования
 * 
 * Применяемые паттерны:
 * - Singleton: ResourceManager, SettingsManager, GameEventManager, DifficultyManager
 * - Observer: GameEventManager для уведомлений о событиях
 * - Strategy: DifficultyStrategy для различных уровней сложности
 * - Factory: CardFactory для создания карточек
 * - State: GameStateManager для управления состояниями игры
 * - Template Method: CardEventHandler для обработки событий карточек
 */
class MemoryGame : JFrame("Игра Мементо"), GameObserver {
    
    // Основные компоненты игры
    private val cards = mutableListOf<MemoryCard>()
    private var firstCard: MemoryCard? = null
    private var secondCard: MemoryCard? = null
    private var isBusy = false
    
    // Игровая статистика
    private var attempts = 0
    private var elapsedSeconds = 0
    private var matchedPairs = 0
    
    // UI компоненты
    private lateinit var timerLabel: JLabel
    private lateinit var attemptsLabel: JLabel
    private lateinit var statusLabel: JLabel
    private lateinit var gamePanel: JPanel
    private lateinit var animationPanel: JPanel
    private lateinit var controlPanel: JPanel
    private lateinit var infoPanel: JPanel
    private lateinit var buttonPanel: JPanel
    
    // Таймер и анимации
    private var timerJob: Job? = null
    private var currentAnimationLabel: JLabel? = null
    
    // Менеджеры (паттерны)
    private val gameStateManager = GameStateManager()
    
    // Пути к ресурсам
    private val imagePaths = listOf(
        "/images/card1.jpg", "/images/card2.jpg", "/images/card3.jpg", "/images/card4.jpg",
        "/images/card5.jpg", "/images/card6.jpg", "/images/card7.jpg", "/images/card8.jpg"
    )
    
    private val animationPaths = listOf(
        "/animations/001.gif", "/animations/002.gif", "/animations/003.gif", "/animations/004.gif",
        "/animations/005.gif", "/animations/006.gif", "/animations/007.gif", "/animations/008.gif",
        "/animations/009.gif", "/animations/010.gif", "/animations/011.gif", "/animations/012.gif",
        "/animations/013.gif", "/animations/014.gif", "/animations/015.gif", "/animations/016.gif",
        "/animations/017.gif", "/animations/018.gif", "/animations/019.gif", "/animations/020.gif"
    )
    
    private val specialAnimations = mapOf(
        "match" to "/animations/Jake laugh.gif",
        "win" to "/animations/Jake dance.gif",
        "miss" to "/animations/Jake vig eyes.gif",
        "start" to "/animations/Jake.gif"
    )
    
    init {
        // Подписываемся на события игры
        GameEventManager.subscribe(this)
        
        setupUI()
        initGame()
        startTimer()
        showAnimation("start")
    }
    
    /**
     * Настройка пользовательского интерфейса
     */
    private fun setupUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout(10, 10)
        
        setupInfoPanel()
        setupControlPanel()
        setupGamePanel()
        setupAnimationPanel()
        setupButtonPanel()
        
        val centerPanel = JPanel(BorderLayout(10, 0))
        centerPanel.background = Color(30, 30, 40)
        centerPanel.add(gamePanel, BorderLayout.CENTER)
        centerPanel.add(animationPanel, BorderLayout.EAST)
        
        val mainPanel = JPanel(BorderLayout())
        mainPanel.background = Color(30, 30, 40)
        
        mainPanel.add(infoPanel, BorderLayout.NORTH)
        mainPanel.add(controlPanel, BorderLayout.CENTER)
        mainPanel.add(centerPanel, BorderLayout.CENTER)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        
        add(mainPanel)
        
        setSize(1200, 900)
        setLocationRelativeTo(null)
        background = Color(30, 30, 40)
    }
    
    /**
     * Настройка панели информации
     */
    private fun setupInfoPanel() {
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
    }
    
    /**
     * Настройка панели управления
     */
    private fun setupControlPanel() {
        controlPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 5))
        controlPanel.background = Color(40, 40, 50)
        
        val animToggleButton = JButton("🎬 Анимации: ВКЛ").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                SettingsManager.toggleAnimations()
                text = if (SettingsManager.animationsEnabled) "🎬 Анимации: ВКЛ" else "🎬 Анимации: ВЫКЛ"
                if (!SettingsManager.animationsEnabled) clearAnimation()
                GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "animations")
            }
        }
        
        val soundToggleButton = JButton("🔊 Звук: ВКЛ").apply {
            font = Font("Arial", Font.BOLD, 12)
            addActionListener {
                SettingsManager.toggleSound()
                text = if (SettingsManager.soundEnabled) "🔊 Звук: ВКЛ" else "🔊 Звук: ВЫКЛ"
                GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "sound")
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
                    0 -> { SettingsManager.setDifficulty(4); text = "📊 Сложность: 4x4" }
                    1 -> { SettingsManager.setDifficulty(6); text = "📊 Сложность: 6x6" }
                    2 -> { SettingsManager.setDifficulty(8); text = "📊 Сложность: 8x8" }
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
                SettingsManager.setTheme(if (SettingsManager.currentTheme == "dark") "light" else "dark")
                text = if (SettingsManager.currentTheme == "dark") "🎨 Тема: Темная" else "🎨 Тема: Светлая"
                applyTheme()
                GameEventManager.notifyObservers(GameEvent.SETTINGS_CHANGED, "theme")
            }
        }
        
        controlPanel.add(animToggleButton)
        controlPanel.add(soundToggleButton)
        controlPanel.add(difficultyButton)
        controlPanel.add(themeButton)
        controlPanel.add(statsButton)
    }
    
    /**
     * Настройка игровой панели
     */
    private fun setupGamePanel() {
        val strategy = DifficultyManager.getCurrentStrategy()
        gamePanel = JPanel(GridLayout(strategy.gridSize, strategy.gridSize, 5, 5))
        gamePanel.background = Color(30, 30, 40)
        gamePanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }
    
    /**
     * Настройка панели анимации
     */
    private fun setupAnimationPanel() {
        animationPanel = JPanel(BorderLayout())
        animationPanel.background = Color(30, 30, 40)
        animationPanel.preferredSize = Dimension(300, 400)
        animationPanel.border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color(100, 100, 100), 2),
            "🎬 Анимация Jake",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            Font("Arial", Font.BOLD, 16),
            Color(255, 215, 0)
        )
    }
    
    /**
     * Настройка панели кнопок
     */
    private fun setupButtonPanel() {
        buttonPanel = JPanel(FlowLayout())
        buttonPanel.background = Color(40, 40, 50)
        
        val newGameButton = JButton("Новая игра").apply {
            font = Font("Arial", Font.BOLD, 14)
            addActionListener { resetGame() }
        }
        
        val exitButton = JButton("Выход").apply {
            font = Font("Arial", Font.BOLD, 14)
            addActionListener { System.exit(0) }
        }
        
        buttonPanel.add(newGameButton)
        buttonPanel.add(exitButton)
    }
    
    /**
     * Инициализация игры с использованием паттернов
     */
    private fun initGame() {
        cards.clear()
        gamePanel.removeAll()
        
        val strategy = DifficultyManager.getCurrentStrategy()
        DifficultyManager.setStrategy(strategy.gridSize)
        
        // Используем CardFactory для создания карточек
        val cardSet = CardFactory.createCardSet(strategy.gridSize, imagePaths)
        
        // Обновляем layout панели игры
        gamePanel.layout = GridLayout(strategy.gridSize, strategy.gridSize, 5, 5)
        
        // Создаем карточки и добавляем их на панель
        cardSet.forEach { card ->
            card.addActionListener(createCardClickListener(card))
            cards.add(card)
            gamePanel.add(card)
        }
        
        matchedPairs = 0
        attempts = 0
        attemptsLabel.text = "Попытки: $attempts"
        statusLabel.text = "Найдите пары! Сложность: ${strategy.gridSize}x${strategy.gridSize}"
        statusLabel.foreground = Color(100, 200, 100)
        
        gamePanel.revalidate()
        gamePanel.repaint()
    }
    
    /**
     * Создает обработчик клика по карточке
     */
    private fun createCardClickListener(card: MemoryCard) = ActionListener {
        if (isBusy || card.isMatched || card.isFlipped) return@ActionListener
        
        when {
            firstCard == null -> {
                firstCard = card
                card.flip()
                playSound("flip")
                GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
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
    
    /**
     * Проверяет совпадение карточек
     */
    fun checkMatch() {
        val first = firstCard ?: return
        val second = secondCard ?: return
        
        isBusy = true
        
        if (first.getCardId() == second.getCardId()) {
            // Совпадение!
            showAnimation("match")
            playSound("match")
            SwingUtilities.invokeLater {
                first.setMatched()
                second.setMatched()
                matchedPairs++
                statusLabel.text = "✨ Совпадение! ($matchedPairs/${DifficultyManager.getCurrentStrategy().totalPairs}) ✨"
                statusLabel.foreground = Color.GREEN
                
                firstCard = null
                secondCard = null
                isBusy = false
                
                checkWin()
            }
            GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, matchedPairs)
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
            GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, attempts)
        }
    }
    
    /**
     * Проверяет условие победы
     */
    private fun checkWin() {
        val strategy = DifficultyManager.getCurrentStrategy()
        if (strategy.isGameWon(matchedPairs)) {
            timerJob?.cancel()
            showAnimation("win")
            playSound("win")
            statusLabel.text = "🎉 ПОБЕДА! 🎉"
            statusLabel.foreground = Color.YELLOW
            
            // Обновляем статистику через SettingsManager
            SettingsManager.updateGameStats(elapsedSeconds, matchedPairs)
            
            val rating = strategy.getGameRating(elapsedSeconds, attempts)
            val message = """
                🎉 Поздравляем! Вы выиграли! 🎉
                
                ⏱️ Время: ${formatTime(elapsedSeconds)}
                🎯 Попытки: $attempts
                📊 Сложность: ${strategy.gridSize}x${strategy.gridSize}
                ⭐ Оценка: ${"⭐".repeat(rating)}
                
                🏆 Лучшее время: ${if (SettingsManager.bestTime == Int.MAX_VALUE) "Нет" else formatTime(SettingsManager.bestTime)}
                🎮 Игр сыграно: ${SettingsManager.gamesPlayed}
                
                ${if (SettingsManager.achievements.isNotEmpty()) "🏅 Достижения: ${SettingsManager.achievements.joinToString(", ")}" else ""}
                
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
    
    /**
     * Запускает таймер игры
     */
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
    
    /**
     * Обновляет метку таймера
     */
    private fun updateTimerLabel() {
        timerLabel.text = "Время: ${formatTime(elapsedSeconds)}"
    }
    
    /**
     * Обновляет метку попыток
     */
    private fun updateAttemptsLabel() {
        attemptsLabel.text = "Попытки: $attempts"
    }
    
    /**
     * Форматирует время в MM:SS
     */
    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    /**
     * Сбрасывает игру
     */
    fun resetGame() {
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
        gameStateManager.resetToIdle()
    }
    
    /**
     * Показывает анимацию
     */
    private fun showAnimation(eventType: String) {
        if (!SettingsManager.animationsEnabled) return
        
        val animationPath = specialAnimations[eventType] ?: animationPaths.random()
        showGifAnimation(animationPath)
    }
    
    /**
     * Показывает GIF анимацию
     */
    private fun showGifAnimation(animationPath: String) {
        SwingUtilities.invokeLater {
            try {
                currentAnimationLabel?.let { animationPanel.remove(it) }
                
                val animationIcon = ResourceManager.getAnimation(animationPath)
                currentAnimationLabel = JLabel(animationIcon).apply {
                    horizontalAlignment = SwingConstants.CENTER
                    verticalAlignment = SwingConstants.CENTER
                }
                animationPanel.add(currentAnimationLabel, BorderLayout.CENTER)
                animationPanel.revalidate()
                animationPanel.repaint()
            } catch (e: Exception) {
                println("Ошибка загрузки анимации: $animationPath - ${e.message}")
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
        val message = """
            📊 СТАТИСТИКА ИГРЫ 📊
            
            🎮 Всего игр: ${SettingsManager.gamesPlayed}
            🏆 Лучшее время: ${if (SettingsManager.bestTime == Int.MAX_VALUE) "Нет" else formatTime(SettingsManager.bestTime)}
            🎯 Всего совпадений: ${SettingsManager.totalMatches}
            
            🏅 Достижения (${SettingsManager.achievements.size}):
            ${if (SettingsManager.achievements.isEmpty()) "Пока нет достижений" else SettingsManager.achievements.joinToString("\n")}
            
            🎨 Текущие настройки:
            • Анимации: ${if (SettingsManager.animationsEnabled) "Включены" else "Выключены"}
            • Звук: ${if (SettingsManager.soundEnabled) "Включен" else "Выключен"}
            • Сложность: ${SettingsManager.difficulty}x${SettingsManager.difficulty}
            
            ${DifficultyManager.getCurrentStrategyInfo()}
        """.trimIndent()
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "📊 Статистика",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
    
    /**
     * Воспроизводит звуковой эффект
     */
    private fun playSound(soundType: String) {
        if (!SettingsManager.soundEnabled) return
        
        when (soundType) {
            "match" -> println("🔊 Звук совпадения")
            "miss" -> println("🔊 Звук промаха")
            "win" -> println("🔊 Звук победы")
            "flip" -> println("🔊 Звук переворота карты")
        }
    }
    
    /**
     * Применяет выбранную тему оформления
     */
    private fun applyTheme() {
        when (SettingsManager.currentTheme) {
            "dark" -> {
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
        
        repaint()
    }
    
    // Методы для работы с состоянием игры (используются паттернами)
    fun setFirstCard(card: MemoryCard) { firstCard = card }
    fun setSecondCard(card: MemoryCard) { secondCard = card }
    fun getFirstCard(): MemoryCard? = firstCard
    fun getSecondCard(): MemoryCard? = secondCard
    fun clearSelectedCards() { firstCard = null; secondCard = null }
    fun incrementMatchedPairs() { matchedPairs++ }
    fun incrementAttempts() { attempts++; updateAttemptsLabel() }
    fun getMatchedPairs(): Int = matchedPairs
    fun getAttempts(): Int = attempts
    fun setState(state: GameState) { gameStateManager.setState(state) }
    fun resetGameState() { 
        attempts = 0
        elapsedSeconds = 0
        matchedPairs = 0
        firstCard = null
        secondCard = null
        isBusy = false
    }
    
    // Реализация GameObserver
    override fun onGameEvent(event: GameEvent, data: Any?) {
        when (event) {
            GameEvent.CARD_FLIPPED -> {
                playSound("flip")
            }
            GameEvent.CARDS_MATCHED -> {
                showAnimation("match")
                playSound("match")
                statusLabel.text = "✨ Совпадение! ($matchedPairs/${DifficultyManager.getCurrentStrategy().totalPairs}) ✨"
                statusLabel.foreground = Color.GREEN
                checkWin()
            }
            GameEvent.CARDS_MISMATCHED -> {
                showAnimation("miss")
                playSound("miss")
                statusLabel.text = "Не совпало! Попробуйте еще"
                statusLabel.foreground = Color.ORANGE
            }
            GameEvent.GAME_WON -> {
                showAnimation("win")
                playSound("win")
            }
            GameEvent.GAME_RESET -> {
                statusLabel.text = "Найдите пары!"
                statusLabel.foreground = Color(100, 200, 100)
            }
            GameEvent.SETTINGS_CHANGED -> {
                // Настройки уже обновлены в SettingsManager
            }
            GameEvent.ACHIEVEMENT_UNLOCKED -> {
                // Достижения обрабатываются в SettingsManager
            }
        }
    }
}