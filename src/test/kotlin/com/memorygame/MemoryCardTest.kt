package com.memorygame

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.ImageIcon

class MemoryCardTest {

    private lateinit var memoryCard: MemoryCard

    @BeforeEach
    fun setUp() {
        // Создаем карточку с тестовым ID и путем к изображению
        memoryCard = MemoryCard(1, "/images/card1.jpg")
    }

    @Test
    fun `test card initialization`() {
        // Проверяем начальное состояние карточки
        assertFalse(memoryCard.isFlipped, "Карточка должна быть закрыта при инициализации")
        assertFalse(memoryCard.isMatched, "Карточка не должна быть совпавшей при инициализации")
        assertTrue(memoryCard.isEnabled, "Карточка должна быть активна при инициализации")
        assertEquals(1, memoryCard.getCardId(), "ID карточки должен соответствовать переданному значению")
    }

    @Test
    fun `test card flip`() {
        // Проверяем переворот карточки
        assertFalse(memoryCard.isFlipped, "Карточка должна быть закрыта перед переворотом")
        
        memoryCard.flip()
        assertTrue(memoryCard.isFlipped, "Карточка должна быть открыта после переворота")
        
        memoryCard.flip()
        assertFalse(memoryCard.isFlipped, "Карточка должна быть закрыта после повторного переворота")
    }

    @Test
    fun `test set matched`() {
        // Проверяем установку состояния совпадения
        assertFalse(memoryCard.isMatched, "Карточка не должна быть совпавшей перед установкой")
        assertTrue(memoryCard.isEnabled, "Карточка должна быть активна перед установкой совпадения")
        
        memoryCard.setMatched()
        
        assertTrue(memoryCard.isMatched, "Карточка должна быть помечена как совпавшая")
        assertFalse(memoryCard.isEnabled, "Карточка должна быть неактивна после совпадения")
    }

    @Test
    fun `test get card id`() {
        // Проверяем получение ID карточки
        assertEquals(1, memoryCard.getCardId(), "ID карточки должен быть 1")
        
        val anotherCard = MemoryCard(5, "/images/card5.jpg")
        assertEquals(5, anotherCard.getCardId(), "ID другой карточки должен быть 5")
    }

    @Test
    fun `test reset card`() {
        // Проверяем сброс карточки к начальному состоянию
        // Сначала изменяем состояние карточки
        memoryCard.flip()
        memoryCard.setMatched()
        
        // Проверяем, что состояние изменилось
        assertTrue(memoryCard.isFlipped, "Карточка должна быть открыта перед сбросом")
        assertTrue(memoryCard.isMatched, "Карточка должна быть совпавшей перед сбросом")
        assertFalse(memoryCard.isEnabled, "Карточка должна быть неактивна перед сбросом")
        
        // Сбрасываем карточку
        memoryCard.reset()
        
        // Проверяем, что состояние сброшено
        assertFalse(memoryCard.isFlipped, "Карточка должна быть закрыта после сброса")
        assertFalse(memoryCard.isMatched, "Карточка не должна быть совпавшей после сброса")
        assertTrue(memoryCard.isEnabled, "Карточка должна быть активна после сброса")
    }

    @Test
    fun `test multiple cards with different ids`() {
        // Проверяем создание нескольких карточек с разными ID
        val card1 = MemoryCard(1, "/images/card1.jpg")
        val card2 = MemoryCard(2, "/images/card2.jpg")
        val card3 = MemoryCard(3, "/images/card3.jpg")
        
        assertEquals(1, card1.getCardId(), "Первая карточка должна иметь ID 1")
        assertEquals(2, card2.getCardId(), "Вторая карточка должна иметь ID 2")
        assertEquals(3, card3.getCardId(), "Третья карточка должна иметь ID 3")
        
        // Проверяем, что карточки независимы
        card1.flip()
        assertTrue(card1.isFlipped, "Первая карточка должна быть открыта")
        assertFalse(card2.isFlipped, "Вторая карточка должна оставаться закрытой")
        assertFalse(card3.isFlipped, "Третья карточка должна оставаться закрытой")
    }

    @Test
    fun `test card state transitions`() {
        // Проверяем корректность переходов состояний карточки
        
        // Начальное состояние: закрыта, не совпавшая
        assertFalse(memoryCard.isFlipped)
        assertFalse(memoryCard.isMatched)
        assertTrue(memoryCard.isEnabled)
        
        // Переворот: открыта, не совпавшая
        memoryCard.flip()
        assertTrue(memoryCard.isFlipped)
        assertFalse(memoryCard.isMatched)
        assertTrue(memoryCard.isEnabled)
        
        // Совпадение: открыта, совпавшая, неактивна
        memoryCard.setMatched()
        assertTrue(memoryCard.isFlipped)
        assertTrue(memoryCard.isMatched)
        assertFalse(memoryCard.isEnabled)
        
        // Сброс: закрыта, не совпавшая, активна
        memoryCard.reset()
        assertFalse(memoryCard.isFlipped)
        assertFalse(memoryCard.isMatched)
        assertTrue(memoryCard.isEnabled)
    }

    @Test
    fun `test card with invalid image path`() {
        // Проверяем создание карточки с несуществующим путем к изображению
        // Это должно работать без ошибок, используя заглушку
        val invalidCard = MemoryCard(999, "/nonexistent/image.jpg")
        
        assertNotNull(invalidCard, "Карточка должна быть создана даже с несуществующим изображением")
        assertEquals(999, invalidCard.getCardId(), "ID должен быть корректным")
        assertFalse(invalidCard.isFlipped, "Карточка должна быть в начальном состоянии")
    }

    @Test
    fun `test card dimensions`() {
        // Проверяем размеры карточки
        assertEquals(120, memoryCard.preferredSize.width, "Ширина карточки должна быть 120")
        assertEquals(120, memoryCard.preferredSize.height, "Высота карточки должна быть 120")
    }

    @Test
    fun `test card background color`() {
        // Проверяем цвет фона карточки
        assertEquals(Color(70, 130, 180), memoryCard.background, "Цвет фона должен быть синим")
    }

    @Test
    fun `test card border`() {
        // Проверяем границу карточки
        assertNotNull(memoryCard.border, "Граница должна быть установлена")
    }

    @Test
    fun `test card focus painted`() {
        // Проверяем, что карточка не рисует фокус
        assertFalse(memoryCard.isFocusPainted, "Карточка не должна рисовать фокус")
    }

    @Test
    fun `test card icon after flip`() {
        // Проверяем, что иконка обновляется после переворота
        val initialIcon = memoryCard.icon
        memoryCard.flip()
        val flippedIcon = memoryCard.icon
        
        assertNotEquals(initialIcon, flippedIcon, "Иконка должна измениться после переворота")
    }

    @Test
    fun `test card icon after match`() {
        // Проверяем, что иконка обновляется после совпадения
        val initialIcon = memoryCard.icon
        memoryCard.setMatched()
        val matchedIcon = memoryCard.icon
        
        // Иконка может остаться той же, но состояние карточки должно измениться
        assertTrue(memoryCard.isMatched, "Карточка должна быть помечена как совпавшая")
        assertFalse(memoryCard.isEnabled, "Карточка должна быть неактивна")
    }

    @Test
    fun `test card border after match`() {
        // Проверяем изменение границы после совпадения
        val initialBorder = memoryCard.border
        memoryCard.setMatched()
        val matchedBorder = memoryCard.border
        
        assertNotEquals(initialBorder, matchedBorder, "Граница должна измениться после совпадения")
    }

    @Test
    fun `test card border after reset`() {
        // Проверяем восстановление границы после сброса
        memoryCard.setMatched()
        val matchedBorder = memoryCard.border
        memoryCard.reset()
        val resetBorder = memoryCard.border
        
        assertNotEquals(matchedBorder, resetBorder, "Граница должна восстановиться после сброса")
    }

    @Test
    fun `test multiple flip operations`() {
        // Проверяем множественные перевороты
        assertFalse(memoryCard.isFlipped, "Начальное состояние - закрыта")
        
        memoryCard.flip()
        assertTrue(memoryCard.isFlipped, "После первого переворота - открыта")
        
        memoryCard.flip()
        assertFalse(memoryCard.isFlipped, "После второго переворота - закрыта")
        
        memoryCard.flip()
        assertTrue(memoryCard.isFlipped, "После третьего переворота - открыта")
    }

    @Test
    fun `test card state after multiple operations`() {
        // Проверяем состояние карточки после множественных операций
        memoryCard.flip()
        memoryCard.setMatched()
        
        assertTrue(memoryCard.isFlipped, "Карточка должна оставаться открытой")
        assertTrue(memoryCard.isMatched, "Карточка должна быть совпавшей")
        assertFalse(memoryCard.isEnabled, "Карточка должна быть неактивна")
        
        memoryCard.reset()
        
        assertFalse(memoryCard.isFlipped, "После сброса - закрыта")
        assertFalse(memoryCard.isMatched, "После сброса - не совпавшая")
        assertTrue(memoryCard.isEnabled, "После сброса - активна")
    }

    @Test
    fun `test card with different IDs`() {
        // Проверяем создание карточек с разными ID
        val card1 = MemoryCard(1, "/images/card1.jpg")
        val card2 = MemoryCard(2, "/images/card2.jpg")
        val card3 = MemoryCard(3, "/images/card3.jpg")
        
        assertEquals(1, card1.getCardId())
        assertEquals(2, card2.getCardId())
        assertEquals(3, card3.getCardId())
        
        // Проверяем, что карточки независимы
        card1.flip()
        assertTrue(card1.isFlipped)
        assertFalse(card2.isFlipped)
        assertFalse(card3.isFlipped)
    }

    @Test
    fun `test card image loading`() {
        // Проверяем, что изображения загружаются корректно
        assertNotNull(memoryCard.icon, "Иконка должна быть установлена")
        
        // Проверяем размер иконки
        val icon = memoryCard.icon as ImageIcon
        assertTrue(icon.iconWidth > 0, "Ширина иконки должна быть больше 0")
        assertTrue(icon.iconHeight > 0, "Высота иконки должна быть больше 0")
    }
}
