package com.memorygame

import java.awt.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JButton

/**
 * MemoryCard - класс карточки для игры "Мементо"
 * 
 * Представляет отдельную карточку в игре с возможностью переворота,
 * совпадения и сброса состояния.
 * 
 * @param cardId уникальный идентификатор карточки
 * @param imagePath путь к изображению карточки
 */
class MemoryCard(
    private val cardId: Int,
    private val imagePath: String
) : JButton() {
    
    var isFlipped = false
        private set
    
    var isMatched = false
        private set
    
    private val frontImage: Image
    private val backImage: Image
    
    private val cardWidth = 120
    private val cardHeight = 120
    
    init {
        // Используем ResourceManager для загрузки изображения (паттерн Singleton)
        frontImage = ResourceManager.getCardImage(imagePath)
        
        // Создаем изображение для задней стороны (вопросительный знак)
        backImage = createBackImage()
        
        // Настройка внешнего вида
        preferredSize = Dimension(cardWidth, cardHeight)
        background = Color(70, 130, 180)
        border = BorderFactory.createLineBorder(Color.WHITE, 2)
        isFocusPainted = false
        
        // Показываем заднюю сторону
        updateImage()
    }
    
    private fun createBackImage(): Image {
        val img = BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB)
        val g = img.createGraphics()
        
        // Фон карты
        g.color = Color(70, 130, 180)
        g.fillRoundRect(0, 0, cardWidth, cardHeight, 20, 20)
        
        // Рисуем вопросительный знак
        g.color = Color.WHITE
        g.font = Font("Arial", Font.BOLD, 60)
        val fm = g.fontMetrics
        val text = "?"
        val x = (cardWidth - fm.stringWidth(text)) / 2
        val y = (cardHeight + fm.ascent - fm.descent) / 2
        g.drawString(text, x, y)
        
        g.dispose()
        return img
    }
    
    /**
     * Переворачивает карточку (открывает/закрывает)
     */
    fun flip() {
        isFlipped = !isFlipped
        updateImage()
    }
    
    /**
     * Помечает карточку как совпавшую и деактивирует её
     */
    fun setMatched() {
        isMatched = true
        isEnabled = false
        border = BorderFactory.createLineBorder(Color.GREEN, 3)
    }
    
    private fun updateImage() {
        val image = if (isFlipped || isMatched) frontImage else backImage
        icon = ImageIcon(image)
    }
    
    /**
     * Возвращает уникальный идентификатор карточки
     * @return ID карточки
     */
    fun getCardId() = cardId
    
    /**
     * Сбрасывает карточку к начальному состоянию
     */
    fun reset() {
        isFlipped = false
        isMatched = false
        isEnabled = true
        border = BorderFactory.createLineBorder(Color.WHITE, 2)
        updateImage()
    }
}

