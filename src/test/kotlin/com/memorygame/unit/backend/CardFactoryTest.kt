package com.memorygame.unit.backend

import com.memorygame.backend.CardFactory
import com.memorygame.ui.MemoryCard
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для CardFactory
 * 
 * Тестирует фабрику создания карточек и все её методы.
 */
class CardFactoryTest {

    private val testImagePaths = listOf(
        "/images/card1.jpg",
        "/images/card2.jpg",
        "/images/card3.jpg",
        "/images/card4.jpg"
    )

    @Test
    fun `test create single card`() {
        val card = CardFactory.createCard(1, "/images/card1.jpg")
        
        assertNotNull(card)
        assertEquals(1, card.getCardId())
    }

    @Test
    fun `test create card pair`() {
        val pair = CardFactory.createCardPair(1, "/images/card1.jpg")
        
        assertEquals(2, pair.size)
        assertEquals(1, pair[0].getCardId())
        assertEquals(1, pair[1].getCardId())
        assertNotSame(pair[0], pair[1]) // Разные объекты
    }

    @Test
    fun `test create card set 4x4`() {
        val cards = CardFactory.createCardSet(4, testImagePaths)
        
        assertEquals(16, cards.size) // 4x4 = 16 карточек
        assertEquals(8, cards.map { it.getCardId() }.toSet().size) // 8 уникальных пар
        
        // Проверяем, что каждая пара встречается ровно 2 раза
        val idCounts = cards.groupBy { it.getCardId() }.mapValues { it.value.size }
        idCounts.values.forEach { assertEquals(2, it) }
    }

    @Test
    fun `test create card set 6x6`() {
        val cards = CardFactory.createCardSet(6, testImagePaths)
        
        assertEquals(36, cards.size) // 6x6 = 36 карточек
        assertEquals(18, cards.map { it.getCardId() }.toSet().size) // 18 уникальных пар
        
        // Проверяем, что каждая пара встречается ровно 2 раза
        val idCounts = cards.groupBy { it.getCardId() }.mapValues { it.value.size }
        idCounts.values.forEach { assertEquals(2, it) }
    }

    @Test
    fun `test create card set 8x8`() {
        val cards = CardFactory.createCardSet(8, testImagePaths)
        
        assertEquals(64, cards.size) // 8x8 = 64 карточки
        assertEquals(32, cards.map { it.getCardId() }.toSet().size) // 32 уникальных пары
        
        // Проверяем, что каждая пара встречается ровно 2 раза
        val idCounts = cards.groupBy { it.getCardId() }.mapValues { it.value.size }
        idCounts.values.forEach { assertEquals(2, it) }
    }

    @Test
    fun `test create card set shuffling`() {
        val cards1 = CardFactory.createCardSet(4, testImagePaths)
        val cards2 = CardFactory.createCardSet(4, testImagePaths)
        
        // Карточки должны быть перемешаны (порядок может отличаться)
        val ids1 = cards1.map { it.getCardId() }
        val ids2 = cards2.map { it.getCardId() }
        
        // Проверяем, что количество карточек одинаковое
        assertEquals(ids1.size, ids2.size)
        
        // Проверяем, что наборы ID одинаковые
        assertEquals(ids1.toSet(), ids2.toSet())
    }

    @Test
    fun `test create card with validation valid image`() {
        val card = CardFactory.createCardWithValidation(1, "/images/card1.jpg", true)
        
        assertNotNull(card)
        assertEquals(1, card!!.getCardId())
    }

    @Test
    fun `test create card with validation invalid image`() {
        val card = CardFactory.createCardWithValidation(1, "/images/nonexistent.jpg", true)
        
        // Должна быть создана карточка даже с несуществующим изображением
        assertNotNull(card)
        assertEquals(1, card!!.getCardId())
    }

    @Test
    fun `test create card with validation disabled`() {
        val card = CardFactory.createCardWithValidation(1, "/images/nonexistent.jpg", false)
        
        assertNotNull(card)
        assertEquals(1, card!!.getCardId())
    }

    @Test
    fun `test create default card`() {
        val card1 = CardFactory.createDefaultCard(0)
        val card2 = CardFactory.createDefaultCard(8)
        val card3 = CardFactory.createDefaultCard(15)
        
        assertEquals(0, card1.getCardId())
        assertEquals(8, card2.getCardId()) // cardId передается как есть
        assertEquals(15, card3.getCardId()) // cardId передается как есть
    }

    @Test
    fun `test get card stats`() {
        val cards = CardFactory.createCardSet(4, testImagePaths)
        val stats = CardFactory.getCardStats(cards)
        
        assertTrue(stats.contains("Total cards: 16"))
        assertTrue(stats.contains("Unique pairs: 8"))
        assertTrue(stats.contains("Cards per pair: 2"))
        assertTrue(stats.contains("Grid size: 4x4"))
    }

    @Test
    fun `test get card stats 6x6`() {
        val cards = CardFactory.createCardSet(6, testImagePaths)
        val stats = CardFactory.getCardStats(cards)
        
        assertTrue(stats.contains("Total cards: 36"))
        assertTrue(stats.contains("Unique pairs: 18"))
        assertTrue(stats.contains("Cards per pair: 2"))
        assertTrue(stats.contains("Grid size: 6x6"))
    }

    @Test
    fun `test get card stats 8x8`() {
        val cards = CardFactory.createCardSet(8, testImagePaths)
        val stats = CardFactory.getCardStats(cards)
        
        assertTrue(stats.contains("Total cards: 64"))
        assertTrue(stats.contains("Unique pairs: 32"))
        assertTrue(stats.contains("Cards per pair: 2"))
        assertTrue(stats.contains("Grid size: 8x8"))
    }

    @Test
    fun `test create card set with limited images`() {
        val limitedPaths = listOf("/images/card1.jpg", "/images/card2.jpg")
        val cards = CardFactory.createCardSet(4, limitedPaths)
        
        assertEquals(16, cards.size)
        assertEquals(8, cards.map { it.getCardId() }.toSet().size)
        
        // Проверяем, что изображения циклически повторяются
        val imageIds = cards.map { it.getCardId() }
        assertTrue(imageIds.contains(0)) // card1.jpg
        assertTrue(imageIds.contains(1)) // card2.jpg
        assertTrue(imageIds.contains(2)) // card1.jpg (повтор)
        assertTrue(imageIds.contains(3)) // card2.jpg (повтор)
    }

    @Test
    fun `test create card set with single image`() {
        val singlePath = listOf("/images/card1.jpg")
        val cards = CardFactory.createCardSet(4, singlePath)
        
        assertEquals(16, cards.size)
        assertEquals(8, cards.map { it.getCardId() }.toSet().size)
        
        // Все карточки должны использовать одно изображение
        val imageIds = cards.map { it.getCardId() }
        assertEquals(setOf(0, 1, 2, 3, 4, 5, 6, 7), imageIds.toSet())
    }

    @Test
    fun `test factory singleton behavior`() {
        val factory1 = CardFactory
        val factory2 = CardFactory
        
        assertSame(factory1, factory2)
    }

    @Test
    fun `test create card set edge cases`() {
        // Тест с пустым списком изображений
        val emptyPaths = emptyList<String>()
        val cards = CardFactory.createCardSet(4, emptyPaths)
        
        assertEquals(16, cards.size)
        assertEquals(8, cards.map { it.getCardId() }.toSet().size)
    }
}
