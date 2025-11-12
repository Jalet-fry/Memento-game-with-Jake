package com.memorygame.ui

import com.memorygame.backend.StatisticsManager
import com.memorygame.data.PlayerProfile
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 * PlayerSelectionDialog - диалог выбора и создания игрока
 * 
 * Позволяет игроку:
 * - Выбрать существующего игрока
 * - Создать нового игрока
 * - Удалить игрока
 * - Установить текущего игрока
 * 
 * Применяет паттерн Dialog для взаимодействия с пользователем.
 */
class PlayerSelectionDialog(parent: JFrame) : JDialog(parent, "Выбор игрока", true) {
    
    private var selectedPlayer: PlayerProfile? = null
    private var playerList: JList<String>? = null
    private var playerListModel: DefaultListModel<String> = DefaultListModel()
    private var newPlayerField: JTextField? = null
    private var createButton: JButton? = null
    private var selectButton: JButton? = null
    private var deleteButton: JButton? = null
    
    init {
        setupUI()
        loadPlayers()
        pack()
        setLocationRelativeTo(parent)
        
        // Добавляем обработчик закрытия окна
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                // При закрытии через крестик:
                // 1. Если selectedPlayer уже установлен (игрок был создан или выбран), оставляем его
                // 2. Если selectedPlayer не установлен, но есть текущий игрок в StatisticsManager, используем его
                // Это гарантирует, что при закрытии диалога без явного выбора будет использован текущий игрок
                if (selectedPlayer == null) {
                    val currentPlayer = StatisticsManager.getCurrentPlayer()
                    if (currentPlayer != null) {
                        selectedPlayer = currentPlayer
                    }
                }
            }
        })
    }
    
    /**
     * Настраивает пользовательский интерфейс
     */
    private fun setupUI() {
        layout = BorderLayout(10, 10)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        
        // Панель со списком игроков
        val listPanel = JPanel(BorderLayout(10, 10))
        listPanel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Существующие игроки",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP
        )
        
        playerList = JList(playerListModel).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            font = Font("Arial", Font.PLAIN, 14)
            addListSelectionListener {
                updateButtons()
            }
        }
        
        val scrollPane = JScrollPane(playerList).apply {
            preferredSize = Dimension(300, 200)
        }
        
        listPanel.add(scrollPane, BorderLayout.CENTER)
        
        // Панель создания нового игрока
        val createPanel = JPanel(BorderLayout(10, 10))
        createPanel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Создать нового игрока",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP
        )
        
        newPlayerField = JTextField(20).apply {
            font = Font("Arial", Font.PLAIN, 14)
            addActionListener {
                createPlayer()
            }
        }
        
        createButton = JButton("Создать").apply {
            font = Font("Arial", Font.BOLD, 14)
            addActionListener {
                createPlayer()
            }
        }
        
        val createInputPanel = JPanel(BorderLayout(10, 5))
        createInputPanel.add(JLabel("Имя игрока:"), BorderLayout.NORTH)
        createInputPanel.add(newPlayerField, BorderLayout.CENTER)
        createInputPanel.add(createButton, BorderLayout.EAST)
        
        createPanel.add(createInputPanel, BorderLayout.CENTER)
        
        // Панель кнопок
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 10))
        
        selectButton = JButton("Играть").apply {
            font = Font("Arial", Font.BOLD, 14)
            isEnabled = false
            addActionListener {
                selectPlayer()
            }
        }
        
        deleteButton = JButton("Удалить").apply {
            font = Font("Arial", Font.PLAIN, 14)
            isEnabled = false
            addActionListener {
                deletePlayer()
            }
        }
        
        val cancelButton = JButton("Отмена").apply {
            font = Font("Arial", Font.PLAIN, 14)
            addActionListener {
                dispose()
            }
        }
        
        buttonPanel.add(selectButton)
        buttonPanel.add(deleteButton)
        buttonPanel.add(cancelButton)
        
        // Основная панель
        val mainPanel = JPanel(BorderLayout(10, 10))
        mainPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        mainPanel.add(listPanel, BorderLayout.CENTER)
        mainPanel.add(createPanel, BorderLayout.SOUTH)
        
        add(mainPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
        
        // Выделяем текущего игрока
        val currentPlayer = StatisticsManager.getCurrentPlayer()
        if (currentPlayer != null) {
            val index = playerListModel.indexOf(currentPlayer.name)
            if (index >= 0) {
                playerList?.selectedIndex = index
                playerList?.ensureIndexIsVisible(index)
            }
        }
    }
    
    /**
     * Загружает список игроков
     */
    private fun loadPlayers() {
        playerListModel.clear()
        val players = StatisticsManager.getAllPlayers()
        players.forEach { player ->
            playerListModel.addElement(player.name)
        }
        updateButtons()
    }
    
    /**
     * Обновляет состояние кнопок
     */
    private fun updateButtons() {
        val selectedIndex = playerList?.selectedIndex ?: -1
        val hasSelection = selectedIndex >= 0
        
        selectButton?.isEnabled = hasSelection
        deleteButton?.isEnabled = hasSelection
    }
    
    /**
     * Создает нового игрока
     */
    private fun createPlayer() {
        val playerName = newPlayerField?.text?.trim() ?: return
        
        if (playerName.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Имя игрока не может быть пустым!",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        if (StatisticsManager.playerExists(playerName)) {
            JOptionPane.showMessageDialog(
                this,
                "Игрок с таким именем уже существует!",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        val profile = StatisticsManager.createPlayer(playerName)
        if (profile != null) {
            // Явно устанавливаем созданного игрока как текущего
            StatisticsManager.setCurrentPlayer(playerName)
            selectedPlayer = profile
            
            loadPlayers()
            newPlayerField?.text = ""
            
            // Выделяем созданного игрока
            val index = playerListModel.indexOf(playerName)
            if (index >= 0) {
                playerList?.selectedIndex = index
                playerList?.ensureIndexIsVisible(index)
            }
            
            JOptionPane.showMessageDialog(
                this,
                "Игрок '$playerName' успешно создан и выбран для игры!",
                "Успех",
                JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Не удалось создать игрока!",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
    
    /**
     * Выбирает игрока и закрывает диалог
     */
    private fun selectPlayer() {
        val selectedIndex = playerList?.selectedIndex ?: -1
        if (selectedIndex < 0) return
        
        val playerName = playerListModel.getElementAt(selectedIndex)
        val profile = StatisticsManager.getPlayerProfile(playerName)
        
        if (profile != null) {
            StatisticsManager.setCurrentPlayer(playerName)
            selectedPlayer = profile
            dispose()
        }
    }
    
    /**
     * Удаляет игрока
     */
    private fun deletePlayer() {
        val selectedIndex = playerList?.selectedIndex ?: -1
        if (selectedIndex < 0) return
        
        val playerName = playerListModel.getElementAt(selectedIndex)
        val currentPlayer = StatisticsManager.getCurrentPlayer()
        
        if (currentPlayer?.name == playerName) {
            JOptionPane.showMessageDialog(
                this,
                "Нельзя удалить текущего игрока!",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        val result = JOptionPane.showConfirmDialog(
            this,
            "Вы уверены, что хотите удалить игрока '$playerName'?\nВсе статистические данные будут потеряны!",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        )
        
        if (result == JOptionPane.YES_OPTION) {
            if (StatisticsManager.removePlayer(playerName)) {
                loadPlayers()
                JOptionPane.showMessageDialog(
                    this,
                    "Игрок '$playerName' успешно удален!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE
                )
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Не удалось удалить игрока!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }
    
    /**
     * Получает выбранного игрока
     */
    fun getSelectedPlayer(): PlayerProfile? {
        return selectedPlayer
    }
    
    /**
     * Показывает диалог и возвращает выбранного игрока
     * Блокирует выполнение до закрытия диалога (модальный диалог)
     */
    fun showDialog(): PlayerProfile? {
        setVisible(true) // Блокирует выполнение до закрытия диалога
        return selectedPlayer
    }
}

