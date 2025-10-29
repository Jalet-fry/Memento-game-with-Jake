package com.memorygame.unit.logic

import com.memorygame.ui.MemoryCard
import com.memorygame.ui.MemoryGame
import com.memorygame.logic.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import javax.swing.Timer
import java.lang.reflect.Method

class CardEventHandlerTest {

    private lateinit var card: MemoryCard

    @BeforeEach
    fun setUp() {
        // Don't create MemoryGame to avoid UI creation
        card = MemoryCard(1, "test_image1.jpg")
    }

    @Test
    fun `test CardEventHandler concrete implementations`() {
        // Тестируем конкретные реализации CardEventHandler
        val flipHandler = CardFlipHandler()
        val matchHandler = CardMatchHandler()
        val mismatchHandler = CardMismatchHandler()

        assertNotNull(flipHandler, "CardFlipHandler должен быть создан")
        assertNotNull(matchHandler, "CardMatchHandler должен быть создан")
        assertNotNull(mismatchHandler, "CardMismatchHandler должен быть создан")
    }

    @Test
    fun `test CardFlipHandler creation`() {
        val handler = CardFlipHandler()
        assertNotNull(handler, "CardFlipHandler должен быть создан")
    }

    @Test
    fun `test CardMatchHandler creation`() {
        val handler = CardMatchHandler()
        assertNotNull(handler, "CardMatchHandler должен быть создан")
    }

    @Test
    fun `test CardMatchHandler canHandleEvent with no cards`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда нет выбранных карт
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно обрабатывать когда нет карт")
    }

    @Test
    fun `test CardMatchHandler canHandleEvent with one card`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        
        // Устанавливаем только первую карту
        game.setFirstCard(card)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда есть только одна карта
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно обрабатывать когда есть только одна карта")
    }

    @Test
    fun `test CardMatchHandler canHandleEvent with two cards`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(2, "test_image2.jpg")
        
        // Устанавливаем обе карты
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда есть две карты
        assertTrue(method.invoke(handler, game, card) as Boolean, "Должно обрабатывать когда есть две карты")
    }

    @Test
    fun `test CardMatchHandler validateCard with matching cards`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(1, "test_image2.jpg") // Тот же ID
        
        // Устанавливаем карты с одинаковым ID
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с совпадающими картами
        assertTrue(method.invoke(handler, game, card) as Boolean, "Должно валидировать совпадающие карты")
    }

    @Test
    fun `test CardMatchHandler validateCard with different cards`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(2, "test_image2.jpg") // Разный ID
        
        // Устанавливаем карты с разными ID
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с разными картами
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно валидировать разные карты")
    }

    @Test
    fun `test CardMatchHandler validateCard with null cards`() {
        val handler = CardMatchHandler()
        val game = MemoryGame()
        
        // Не устанавливаем карты (null)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с null картами
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно валидировать null карты")
    }

    @Test
    fun `test CardMismatchHandler creation`() {
        val handler = CardMismatchHandler()
        assertNotNull(handler, "CardMismatchHandler должен быть создан")
    }

    @Test
    fun `test CardMismatchHandler canHandleEvent with no cards`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда нет выбранных карт
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно обрабатывать когда нет карт")
    }

    @Test
    fun `test CardMismatchHandler canHandleEvent with one card`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        
        // Устанавливаем только первую карту
        game.setFirstCard(card)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда есть только одна карта
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно обрабатывать когда есть только одна карта")
    }

    @Test
    fun `test CardMismatchHandler canHandleEvent with two cards`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(2, "test_image2.jpg")
        
        // Устанавливаем обе карты
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("canHandleEvent", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем когда есть две карты
        assertTrue(method.invoke(handler, game, card) as Boolean, "Должно обрабатывать когда есть две карты")
    }

    @Test
    fun `test CardMismatchHandler validateCard with matching cards`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(1, "test_image2.jpg") // Тот же ID
        
        // Устанавливаем карты с одинаковым ID
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с совпадающими картами
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно валидировать совпадающие карты")
    }

    @Test
    fun `test CardMismatchHandler validateCard with different cards`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        val card2 = MemoryCard(2, "test_image2.jpg") // Разный ID
        
        // Устанавливаем карты с разными ID
        game.setFirstCard(card)
        game.setSecondCard(card2)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с разными картами
        assertTrue(method.invoke(handler, game, card) as Boolean, "Должно валидировать разные карты")
    }

    @Test
    fun `test CardMismatchHandler validateCard with null cards`() {
        val handler = CardMismatchHandler()
        val game = MemoryGame()
        
        // Не устанавливаем карты (null)
        
        // Используем рефлексию для доступа к protected методу
        val method = CardEventHandler::class.java.getDeclaredMethod("validateCard", MemoryGame::class.java, MemoryCard::class.java)
        method.isAccessible = true
        
        // Тестируем валидацию с null картами
        assertFalse(method.invoke(handler, game, card) as Boolean, "Не должно валидировать null карты")
    }

    @Test
    fun `test CardEventHandler inheritance`() {
        // Тестируем наследование
        val flipHandler = CardFlipHandler()
        val matchHandler = CardMatchHandler()
        val mismatchHandler = CardMismatchHandler()

        assertTrue(flipHandler is CardEventHandler, "CardFlipHandler должен наследоваться от CardEventHandler")
        assertTrue(matchHandler is CardEventHandler, "CardMatchHandler должен наследоваться от CardEventHandler")
        assertTrue(mismatchHandler is CardEventHandler, "CardMismatchHandler должен наследоваться от CardEventHandler")
    }

    @Test
    fun `test CardEventHandler polymorphism`() {
        // Тестируем полиморфизм
        val handlers: List<CardEventHandler> = listOf(
            CardFlipHandler(),
            CardMatchHandler(),
            CardMismatchHandler()
        )

        handlers.forEach { handler ->
            assertNotNull(handler, "Все обработчики должны быть созданы")
            assertTrue(handler is CardEventHandler, "Все обработчики должны быть CardEventHandler")
        }
    }
}
