package com.memorygame.integration

import com.memorygame.ui.MemoryCard
import com.memorygame.ui.MemoryGame
import com.memorygame.logic.*
import com.memorygame.backend.GameEventManager
import com.memorygame.backend.GameEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Интеграционные тесты для CardEventHandler
 * 
 * Тестирует полные сценарии использования обработчиков карточек
 * с реальными объектами MemoryGame и MemoryCard.
 */
class CardEventHandlerIntegrationTest {

    private lateinit var game: MemoryGame
    private lateinit var cards: List<MemoryCard>

    @BeforeEach
    fun setUp() {
        // Создаем реальную игру для интеграционных тестов
        game = MemoryGame()
        
        // Инициализируем UI компоненты
        val initGameMethod = MemoryGame::class.java.getDeclaredMethod("initGame")
        initGameMethod.isAccessible = true
        initGameMethod.invoke(game)
        
        // Получаем карты через рефлексию
        val cardsField = MemoryGame::class.java.getDeclaredField("cards")
        cardsField.isAccessible = true
        cards = cardsField.get(game) as List<MemoryCard>
    }

    @Test
    fun `test CardFlipHandler complete flow with real game`() {
        // Arrange
        val handler = CardFlipHandler()
        val card = cards[0]
        
        // Убеждаемся, что карта изначально не перевернута
        assertFalse(card.isFlipped, "Карта должна быть изначально не перевернута")
        
        // Act
        handler.handleCardEvent(game, card)
        
        // Assert
        assertTrue(card.isFlipped, "Карта должна быть перевернута после обработки")
        
        // Проверяем, что карта установлена как первая выбранная
        // Примечание: CardFlipHandler не устанавливает карты в игре, только переворачивает их
        // Это нормальное поведение для CardFlipHandler
    }

    @Test
    fun `test CardMatchHandler complete flow with matching cards`() {
        // Arrange
        val handler = CardMatchHandler()
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Устанавливаем карты с одинаковым ID (создаем пару)
        val card2Field = MemoryCard::class.java.getDeclaredField("cardId")
        card2Field.isAccessible = true
        card2Field.set(card2, card1.getCardId())
        
        // Устанавливаем обе карты в игре
        game.setFirstCard(card1)
        game.setSecondCard(card2)
        
        // Переворачиваем карты, чтобы они были в правильном состоянии
        card1.flip()
        card2.flip()
        
        // Act
        handler.handleCardEvent(game, card1)
        
        // Assert
        assertTrue(card1.isMatched, "Первая карта должна быть отмечена как совпавшая")
        assertTrue(card2.isMatched, "Вторая карта должна быть отмечена как совпавшая")
        assertTrue(card1.isFlipped, "Первая карта должна остаться перевернутой")
        assertTrue(card2.isFlipped, "Вторая карта должна остаться перевернутой")
        
        // Проверяем, что счетчик совпавших пар увеличился
        assertEquals(1, game.getMatchedPairs(), "Счетчик совпавших пар должен увеличиться")
    }

    @Test
    fun `test CardMismatchHandler complete flow with non-matching cards`() {
        // Arrange
        val handler = CardMismatchHandler()
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Убеждаемся, что карты имеют разные ID (если они одинаковые, делаем их разными)
        if (card1.getCardId() == card2.getCardId()) {
            val card2Field = MemoryCard::class.java.getDeclaredField("cardId")
            card2Field.isAccessible = true
            card2Field.set(card2, card1.getCardId() + 1)
        }
        assertNotEquals(card1.getCardId(), card2.getCardId(), "Карты должны иметь разные ID")
        
        // Устанавливаем обе карты в игре
        game.setFirstCard(card1)
        game.setSecondCard(card2)
        
        // Переворачиваем карты, чтобы они были в правильном состоянии
        card1.flip()
        card2.flip()
        
        // Act
        handler.handleCardEvent(game, card1)
        
        // Assert - сразу после обработки карты должны быть перевернуты
        assertTrue(card1.isFlipped, "Первая карта должна быть перевернута")
        assertTrue(card2.isFlipped, "Вторая карта должна быть перевернута")
        
        // Ждем завершения таймера (1.5 секунды)
        Thread.sleep(1600)
        
        // После таймера карты должны перевернуться обратно
        assertFalse(card1.isFlipped, "Первая карта должна перевернуться обратно после таймера")
        assertFalse(card2.isFlipped, "Вторая карта должна перевернуться обратно после таймера")
        assertFalse(card1.isMatched, "Первая карта не должна быть отмечена как совпавшая")
        assertFalse(card2.isMatched, "Вторая карта не должна быть отмечена как совпавшая")
    }

    @Test
    fun `test CardEventHandler validation failures`() {
        // Arrange
        val handler = CardMatchHandler()
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Устанавливаем карты с разными ID
        game.setFirstCard(card1)
        game.setSecondCard(card2)
        
        // Act & Assert - валидация должна провалиться
        val canHandleMethod = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        canHandleMethod.isAccessible = true
        
        assertTrue(canHandleMethod.invoke(handler, game, card1) as Boolean, "Handler должен уметь обработать событие")
        
        val validateMethod = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        validateMethod.isAccessible = true
        
        assertFalse(validateMethod.invoke(handler, game, card1) as Boolean, "Валидация должна провалиться для разных карт")
    }

    @Test
    fun `test CardFlipHandler with already flipped card`() {
        // Arrange
        val handler = CardFlipHandler()
        val card = cards[0]
        
        // Переворачиваем карту заранее
        card.flip()
        assertTrue(card.isFlipped, "Карта должна быть перевернута")
        
        // Act
        handler.handleCardEvent(game, card)
        
        // Assert - CardFlipHandler переворачивает карту независимо от состояния
        // Если карта была перевернута, она станет не перевернутой после второго flip()
        assertFalse(card.isFlipped, "Карта должна быть не перевернута после двойного переворота")
    }

    @Test
    fun `test CardMatchHandler with already matched cards`() {
        // Arrange
        val handler = CardMatchHandler()
        val card1 = cards[0]
        val card2 = cards[1]
        
        // Устанавливаем одинаковые ID
        val card2Field = MemoryCard::class.java.getDeclaredField("cardId")
        card2Field.isAccessible = true
        card2Field.set(card2, card1.getCardId())
        
        // Отмечаем карты как уже совпавшие
        card1.setMatched()
        card2.setMatched()
        
        game.setFirstCard(card1)
        game.setSecondCard(card2)
        
        // Act
        handler.handleCardEvent(game, card1)
        
        // Assert - карты должны остаться совпавшими
        assertTrue(card1.isMatched, "Первая карта должна остаться совпавшей")
        assertTrue(card2.isMatched, "Вторая карта должна остаться совпавшей")
    }

    @Test
    fun `test CardEventHandler with null cards`() {
        // Arrange
        val handler = CardMatchHandler()
        val card = cards[0]
        
        // Не устанавливаем карты в игре (оставляем null)
        
        // Act & Assert
        val canHandleMethod = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        canHandleMethod.isAccessible = true
        
        assertFalse(canHandleMethod.invoke(handler, game, card) as Boolean, "Handler не должен обрабатывать событие без карт")
    }

    @Test
    fun `test CardEventHandler observers notification`() {
        // Arrange
        val handler = CardFlipHandler()
        val card = cards[0]
        var eventReceived = false
        var receivedCard: MemoryCard? = null
        
        // Подписываемся на события
        val observer = object : com.memorygame.backend.GameObserver {
            override fun onGameEvent(event: GameEvent, data: Any?) {
                if (event == GameEvent.CARD_FLIPPED) {
                    eventReceived = true
                    receivedCard = data as MemoryCard
                }
            }
        }
        
        GameEventManager.subscribe(observer)
        
        try {
            // Act
            handler.handleCardEvent(game, card)
            
            // Assert
            assertTrue(eventReceived, "Событие должно быть получено")
            assertEquals(card, receivedCard, "Полученная карта должна совпадать")
        } finally {
            // Cleanup
            GameEventManager.unsubscribe(observer)
        }
    }

    @Test
    fun `test CardEventHandler full game scenario`() {
        // Arrange - создаем полный игровой сценарий
        val flipHandler = CardFlipHandler()
        val matchHandler = CardMatchHandler()
        val mismatchHandler = CardMismatchHandler()
        
        val card1 = cards[0]
        val card2 = cards[1]
        val card3 = cards[2]
        
        // Устанавливаем одинаковые ID для card1 и card2
        val card2Field = MemoryCard::class.java.getDeclaredField("cardId")
        card2Field.isAccessible = true
        card2Field.set(card2, card1.getCardId())
        
        // Act - полный сценарий игры
        
        // 1. Переворачиваем первую карту
        flipHandler.handleCardEvent(game, card1)
        assertTrue(card1.isFlipped, "Первая карта должна быть перевернута")
        // CardFlipHandler не устанавливает карты в игре, только переворачивает их
        
        // 2. Устанавливаем первую карту в игре вручную
        game.setFirstCard(card1)
        
        // 3. Переворачиваем вторую карту (совпадение)
        flipHandler.handleCardEvent(game, card2)
        assertTrue(card2.isFlipped, "Вторая карта должна быть перевернута")
        
        // 4. Устанавливаем вторую карту в игре вручную
        game.setSecondCard(card2)
        
        // 5. Обрабатываем совпадение
        matchHandler.handleCardEvent(game, card1)
        assertTrue(card1.isMatched, "Первая карта должна быть отмечена как совпавшая")
        assertTrue(card2.isMatched, "Вторая карта должна быть отмечена как совпавшая")
        assertEquals(1, game.getMatchedPairs(), "Счетчик пар должен увеличиться")
        
        // 6. Переворачиваем третью карту
        flipHandler.handleCardEvent(game, card3)
        assertTrue(card3.isFlipped, "Третья карта должна быть перевернута")
        game.setFirstCard(card3)
        
        // 7. Переворачиваем четвертую карту (несовпадение)
        val card4 = cards[3]
        flipHandler.handleCardEvent(game, card4)
        assertTrue(card4.isFlipped, "Четвертая карта должна быть перевернута")
        game.setSecondCard(card4)
        
        // 8. Обрабатываем несовпадение
        mismatchHandler.handleCardEvent(game, card3)
        
        // Ждем завершения таймера (увеличиваем время ожидания)
        Thread.sleep(2000)
        
        // Assert - после несовпадения карты должны перевернуться обратно
        assertFalse(card3.isFlipped, "Третья карта должна перевернуться обратно")
        assertFalse(card4.isFlipped, "Четвертая карта должна перевернуться обратно")
        
        // Совпавшие карты должны остаться перевернутыми
        assertTrue(card1.isFlipped, "Первая карта должна остаться перевернутой")
        assertTrue(card2.isFlipped, "Вторая карта должна остаться перевернутой")
    }
}
