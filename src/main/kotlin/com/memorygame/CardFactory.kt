package com.memorygame

/**
 * CardFactory - фабрика для создания карточек с паттерном Factory
 * 
 * Применяет паттерн Factory для централизованного создания
 * и конфигурации карточек игры.
 * 
 * Решает проблему: Инкапсулирует логику создания карточек
 * и обеспечивает единообразное создание объектов.
 */
object CardFactory {
    
    /**
     * Создает карточку с указанным ID и путем к изображению
     * @param cardId уникальный идентификатор карточки
     * @param imagePath путь к изображению карточки
     * @return созданная карточка
     */
    fun createCard(cardId: Int, imagePath: String): MemoryCard {
        return MemoryCard(cardId, imagePath)
    }
    
    /**
     * Создает пару карточек с одинаковым изображением
     * @param pairId идентификатор пары
     * @param imagePath путь к изображению
     * @return список из двух карточек
     */
    fun createCardPair(pairId: Int, imagePath: String): List<MemoryCard> {
        return listOf(
            createCard(pairId, imagePath),
            createCard(pairId, imagePath)
        )
    }
    
    /**
     * Создает полный набор карточек для игры
     * @param gridSize размер сетки (4, 6 или 8)
     * @param imagePaths список путей к изображениям
     * @return перемешанный список всех карточек
     */
    fun createCardSet(gridSize: Int, imagePaths: List<String>): List<MemoryCard> {
        val totalPairs = (gridSize * gridSize) / 2
        val cards = mutableListOf<MemoryCard>()
        
        // Создаем пары карточек
        for (i in 0 until totalPairs) {
            val imagePath = imagePaths[i % imagePaths.size]
            val pair = createCardPair(i, imagePath)
            cards.addAll(pair)
        }
        
        // Перемешиваем карточки
        cards.shuffle()
        
        return cards
    }
    
    /**
     * Создает карточку с валидацией параметров
     * @param cardId идентификатор карточки
     * @param imagePath путь к изображению
     * @param validateImage проверять ли существование изображения
     * @return созданная карточка или null при ошибке
     */
    fun createCardWithValidation(
        cardId: Int, 
        imagePath: String, 
        validateImage: Boolean = true
    ): MemoryCard? {
        return try {
            if (validateImage) {
                val resource = CardFactory::class.java.getResource(imagePath)
                if (resource == null) {
                    println("Warning: Image not found: $imagePath")
                }
            }
            createCard(cardId, imagePath)
        } catch (e: Exception) {
            println("Error creating card: ${e.message}")
            null
        }
    }
    
    /**
     * Создает карточку с настройками по умолчанию
     * @param cardId идентификатор карточки
     * @return карточка с изображением по умолчанию
     */
    fun createDefaultCard(cardId: Int): MemoryCard {
        val defaultImagePath = "/images/card${(cardId % 8) + 1}.jpg"
        return createCard(cardId, defaultImagePath)
    }
    
    /**
     * Получает статистику создания карточек
     * @param cards список карточек
     * @return статистика в виде строки
     */
    fun getCardStats(cards: List<MemoryCard>): String {
        val uniqueIds = cards.map { it.getCardId() }.toSet()
        val totalCards = cards.size
        val totalPairs = uniqueIds.size
        
        return """
            Total cards: $totalCards
            Unique pairs: $totalPairs
            Cards per pair: ${totalCards / totalPairs}
            Grid size: ${kotlin.math.sqrt(totalCards.toDouble()).toInt()}x${kotlin.math.sqrt(totalCards.toDouble()).toInt()}
        """.trimIndent()
    }
}
