package com.memorygame.unit.logic

import com.memorygame.logic.*
import com.memorygame.ui.MemoryGame
import com.memorygame.ui.MemoryCard
import com.memorygame.backend.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Тесты для GameState классов
 * 
 * Тестирует все состояния игры и их переходы.
 */
class GameStateTest {

    private var originalOut: PrintStream? = null
    private var testOut: ByteArrayOutputStream? = null
    private lateinit var game: MemoryGame
    private lateinit var card: MemoryCard

    private fun getGameStateManager(game: MemoryGame): GameStateManager {
        val field = MemoryGame::class.java.getDeclaredField("gameStateManager")
        field.isAccessible = true
        return field.get(game) as GameStateManager
    }

    @BeforeEach
    fun setUp() {
        // Сохраняем оригинальный поток вывода
        originalOut = System.out
        testOut = ByteArrayOutputStream()
        System.setOut(PrintStream(testOut))
        
        // Создаем тестовые объекты
        game = MemoryGame()
        card = MemoryCard(1, "test_image.jpg")
    }

    @AfterEach
    fun tearDown() {
        // Восстанавливаем оригинальный поток вывода
        System.setOut(originalOut)
    }

    @Test
    fun `test IdleState properties`() {
        val state = IdleState()
        
        assertEquals("Ожидание", state.name)
        assertEquals("Игра готова к началу", state.description)
    }

    @Test
    fun `test PlayingState properties`() {
        val state = PlayingState()
        
        assertEquals("Игра", state.name)
        assertEquals("Первая карточка открыта, ожидается вторая", state.description)
    }

    @Test
    fun `test CheckingState properties`() {
        val state = CheckingState()
        
        assertEquals("Проверка", state.name)
        assertEquals("Проверяется совпадение карточек", state.description)
    }

    @Test
    fun `test WonState properties`() {
        val state = WonState()
        
        assertEquals("Победа", state.name)
        assertEquals("Игра завершена победой", state.description)
    }

    @Test
    fun `test GameStateManager initial state`() {
        val manager = GameStateManager()
        
        assertTrue(manager.getCurrentState() is IdleState)
        assertTrue(manager.isInState(IdleState::class.java))
    }

    @Test
    fun `test GameStateManager set state`() {
        val manager = GameStateManager()
        val playingState = PlayingState()
        
        manager.setState(playingState)
        
        assertEquals(playingState, manager.getCurrentState())
        assertTrue(manager.isInState(PlayingState::class.java))
        assertFalse(manager.isInState(IdleState::class.java))
    }

    @Test
    fun `test GameStateManager reset to idle`() {
        val manager = GameStateManager()
        
        manager.setState(PlayingState())
        manager.setState(CheckingState())
        manager.setState(WonState())
        
        manager.resetToIdle()
        
        assertTrue(manager.getCurrentState() is IdleState)
        assertTrue(manager.isInState(IdleState::class.java))
    }

    @Test
    fun `test GameStateManager get state info`() {
        val manager = GameStateManager()
        
        val info = manager.getStateInfo()
        
        assertTrue(info.contains("Текущее состояние"))
        assertTrue(info.contains("Описание"))
        assertTrue(info.contains("Ожидание"))
        assertTrue(info.contains("Игра готова к началу"))
    }

    @Test
    fun `test GameStateManager state transitions`() {
        val manager = GameStateManager()
        
        // Начальное состояние
        assertTrue(manager.getCurrentState() is IdleState)
        
        // Переход в состояние игры
        manager.setState(PlayingState())
        assertTrue(manager.getCurrentState() is PlayingState)
        
        // Переход в состояние проверки
        manager.setState(CheckingState())
        assertTrue(manager.getCurrentState() is CheckingState)
        
        // Переход в состояние победы
        manager.setState(WonState())
        assertTrue(manager.getCurrentState() is WonState)
        
        // Сброс к начальному состоянию
        manager.resetToIdle()
        assertTrue(manager.getCurrentState() is IdleState)
    }

    @Test
    fun `test IdleState handleCardClick with valid card`() {
        val state = IdleState()
        
        // Проверяем начальное состояние
        assertFalse(card.isFlipped)
        assertFalse(card.isMatched)
        
        // Выполняем клик
        state.handleCardClick(game, card)
        
        // Проверяем результат
        assertTrue(card.isFlipped, "Карточка должна быть перевернута")
        assertEquals(card, game.getFirstCard(), "Карточка должна быть установлена как первая")
        assertTrue(getGameStateManager(game).getCurrentState() is PlayingState, "Состояние должно измениться на PlayingState")
    }

    @Test
    fun `test IdleState handleCardClick with flipped card`() {
        val state = IdleState()
        
        // Переворачиваем карточку заранее
        card.flip()
        
        // Выполняем клик
        state.handleCardClick(game, card)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние не должно измениться")
        assertNull(game.getFirstCard(), "Первая карточка не должна быть установлена")
    }

    @Test
    fun `test IdleState handleCardClick with matched card`() {
        val state = IdleState()
        
        // Помечаем карточку как совпавшую
        card.setMatched()
        
        // Выполняем клик
        state.handleCardClick(game, card)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние не должно измениться")
        assertNull(game.getFirstCard(), "Первая карточка не должна быть установлена")
    }

    @Test
    fun `test IdleState handleGameReset`() {
        val state = IdleState()
        
        // Устанавливаем некоторые значения
        game.setFirstCard(card)
        game.setGameState(PlayingState())
        
        // Выполняем сброс
        state.handleGameReset(game)
        
        // Проверяем результат
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние должно быть сброшено на IdleState")
    }

    @Test
    fun `test PlayingState handleCardClick with valid second card`() {
        val state = PlayingState()
        val secondCard = MemoryCard(2, "test_image2.jpg")
        
        // Устанавливаем первую карточку
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Проверяем начальное состояние
        assertFalse(secondCard.isFlipped)
        assertFalse(secondCard.isMatched)
        
        // Выполняем клик по второй карточке
        state.handleCardClick(game, secondCard)
        
        // Проверяем результат
        assertTrue(secondCard.isFlipped, "Вторая карточка должна быть перевернута")
        assertEquals(secondCard, game.getSecondCard(), "Вторая карточка должна быть установлена")
        assertTrue(getGameStateManager(game).getCurrentState() is CheckingState, "Состояние должно измениться на CheckingState")
    }

    @Test
    fun `test PlayingState handleCardClick with same card`() {
        val state = PlayingState()
        
        // Устанавливаем первую карточку
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Выполняем клик по той же карточке
        state.handleCardClick(game, card)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is PlayingState, "Состояние не должно измениться")
        assertNull(game.getSecondCard(), "Вторая карточка не должна быть установлена")
    }

    @Test
    fun `test PlayingState handleCardClick with flipped card`() {
        val state = PlayingState()
        val secondCard = MemoryCard(2, "test_image2.jpg")
        
        // Устанавливаем первую карточку
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Переворачиваем вторую карточку заранее
        secondCard.flip()
        
        // Выполняем клик
        state.handleCardClick(game, secondCard)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is PlayingState, "Состояние не должно измениться")
        assertNull(game.getSecondCard(), "Вторая карточка не должна быть установлена")
    }

    @Test
    fun `test PlayingState handleGameReset`() {
        val state = PlayingState()
        
        // Устанавливаем некоторые значения
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Выполняем сброс
        state.handleGameReset(game)
        
        // Проверяем результат
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние должно быть сброшено на IdleState")
    }

    @Test
    fun `test CheckingState handleCardClick ignores clicks`() {
        val state = CheckingState()
        
        // Устанавливаем карточки
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Выполняем клик
        state.handleCardClick(game, card)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is CheckingState, "Состояние не должно измениться")
    }

    @Test
    fun `test CheckingState handleGameReset`() {
        val state = CheckingState()
        
        // Устанавливаем некоторые значения
        game.setFirstCard(card)
        game.setGameState(state)
        
        // Выполняем сброс
        state.handleGameReset(game)
        
        // Проверяем результат
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние должно быть сброшено на IdleState")
    }

    @Test
    fun `test WonState handleCardClick ignores clicks`() {
        val state = WonState()
        
        // Устанавливаем состояние победы
        game.setGameState(state)
        
        // Выполняем клик
        state.handleCardClick(game, card)
        
        // Проверяем, что состояние не изменилось
        assertTrue(getGameStateManager(game).getCurrentState() is WonState, "Состояние не должно измениться")
    }

    @Test
    fun `test WonState handleGameReset`() {
        val state = WonState()
        
        // Устанавливаем состояние победы
        game.setGameState(state)
        
        // Выполняем сброс
        state.handleGameReset(game)
        
        // Проверяем результат
        assertTrue(getGameStateManager(game).getCurrentState() is IdleState, "Состояние должно быть сброшено на IdleState")
    }

    @Test
    fun `test GameStateManager state change logging`() {
        val manager = GameStateManager()
        
        // Изменяем состояние
        manager.setState(PlayingState())
        
        // Проверяем вывод
        val output = testOut.toString()
        assertTrue(output.contains("State changed: Ожидание -> Игра"), "Должно быть логирование изменения состояния")
    }

    @Test
    fun `test GameStateManager multiple state changes`() {
        val manager = GameStateManager()
        
        // Выполняем несколько изменений состояния
        manager.setState(PlayingState())
        manager.setState(CheckingState())
        manager.setState(WonState())
        
        // Проверяем финальное состояние
        assertTrue(manager.getCurrentState() is WonState)
        assertTrue(manager.isInState(WonState::class.java))
        assertFalse(manager.isInState(IdleState::class.java))
    }

    @Test
    fun `test GameStateManager state info with different states`() {
        val manager = GameStateManager()
        
        // Тестируем с IdleState
        var info = manager.getStateInfo()
        assertTrue(info.contains("Ожидание"))
        assertTrue(info.contains("Игра готова к началу"))
        
        // Тестируем с PlayingState
        manager.setState(PlayingState())
        info = manager.getStateInfo()
        assertTrue(info.contains("Игра"))
        assertTrue(info.contains("Первая карточка открыта"))
        
        // Тестируем с CheckingState
        manager.setState(CheckingState())
        info = manager.getStateInfo()
        assertTrue(info.contains("Проверка"))
        assertTrue(info.contains("Проверяется совпадение"))
        
        // Тестируем с WonState
        manager.setState(WonState())
        info = manager.getStateInfo()
        assertTrue(info.contains("Победа"))
        assertTrue(info.contains("Игра завершена"))
    }

    @Test
    fun `test GameStateManager isInState with all state types`() {
        val manager = GameStateManager()
        
        // Тестируем начальное состояние
        assertTrue(manager.isInState(IdleState::class.java))
        assertFalse(manager.isInState(PlayingState::class.java))
        assertFalse(manager.isInState(CheckingState::class.java))
        assertFalse(manager.isInState(WonState::class.java))
        
        // Тестируем PlayingState
        manager.setState(PlayingState())
        assertFalse(manager.isInState(IdleState::class.java))
        assertTrue(manager.isInState(PlayingState::class.java))
        assertFalse(manager.isInState(CheckingState::class.java))
        assertFalse(manager.isInState(WonState::class.java))
        
        // Тестируем CheckingState
        manager.setState(CheckingState())
        assertFalse(manager.isInState(IdleState::class.java))
        assertFalse(manager.isInState(PlayingState::class.java))
        assertTrue(manager.isInState(CheckingState::class.java))
        assertFalse(manager.isInState(WonState::class.java))
        
        // Тестируем WonState
        manager.setState(WonState())
        assertFalse(manager.isInState(IdleState::class.java))
        assertFalse(manager.isInState(PlayingState::class.java))
        assertFalse(manager.isInState(CheckingState::class.java))
        assertTrue(manager.isInState(WonState::class.java))
    }

    @Test
    fun `test GameState interface implementation`() {
        // Проверяем, что все состояния реализуют интерфейс GameState
        assertTrue(IdleState() is GameState)
        assertTrue(PlayingState() is GameState)
        assertTrue(CheckingState() is GameState)
        assertTrue(WonState() is GameState)
    }

    @Test
    fun `test GameState polymorphism`() {
        val states = listOf(
            IdleState(),
            PlayingState(),
            CheckingState(),
            WonState()
        )
        
        states.forEach { state ->
            assertTrue(state is GameState)
            assertNotNull(state.name)
            assertNotNull(state.description)
            assertTrue(state.name.isNotEmpty())
            assertTrue(state.description.isNotEmpty())
        }
    }
}
