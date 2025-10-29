package com.memorygame.logic

import com.memorygame.ui.MemoryGame
import com.memorygame.ui.MemoryCard
import com.memorygame.backend.GameEventManager
import com.memorygame.backend.GameEvent

/**
 * GameState - интерфейс состояния игры
 * 
 * Применяет паттерн State для управления состояниями игры.
 * 
 * Решает проблему: Инкапсулирует поведение игры в зависимости
 * от текущего состояния и упрощает добавление новых состояний.
 */
interface GameState {
    /**
     * Обрабатывает клик по карточке
     * @param game контекст игры
     * @param card карточка, по которой кликнули
     */
    fun handleCardClick(game: MemoryGame, card: MemoryCard)
    
    /**
     * Обрабатывает сброс игры
     * @param game контекст игры
     */
    fun handleGameReset(game: MemoryGame)
    
    /**
     * Получает название состояния
     */
    val name: String
    
    /**
     * Получает описание состояния
     */
    val description: String
}

/**
 * IdleState - состояние ожидания (начальное состояние)
 */
class IdleState : GameState {
    override val name: String = "Ожидание"
    override val description: String = "Игра готова к началу"
    
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        if (!card.isFlipped && !card.isMatched) {
            card.flip()
            game.setFirstCard(card)
            game.setGameState(PlayingState())
            GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
        }
    }
    
    override fun handleGameReset(game: MemoryGame) {
        game.resetGameState()
        game.setGameState(IdleState())
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
    }
}

/**
 * PlayingState - состояние игры (первая карточка открыта)
 */
class PlayingState : GameState {
    override val name: String = "Игра"
    override val description: String = "Первая карточка открыта, ожидается вторая"
    
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        if (!card.isFlipped && !card.isMatched && card != game.getFirstCard()) {
            card.flip()
            game.setSecondCard(card)
            game.setGameState(CheckingState())
            GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
        }
    }
    
    override fun handleGameReset(game: MemoryGame) {
        game.resetGameState()
        game.setGameState(IdleState())
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
    }
}

/**
 * CheckingState - состояние проверки совпадения
 */
class CheckingState : GameState {
    override val name: String = "Проверка"
    override val description: String = "Проверяется совпадение карточек"
    
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        // В состоянии проверки клики игнорируются
    }
    
    override fun handleGameReset(game: MemoryGame) {
        game.resetGameState()
        game.setGameState(IdleState())
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
    }
}

/**
 * WonState - состояние победы
 */
class WonState : GameState {
    override val name: String = "Победа"
    override val description: String = "Игра завершена победой"
    
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        // В состоянии победы клики игнорируются
    }
    
    override fun handleGameReset(game: MemoryGame) {
        game.resetGameState()
        game.setGameState(IdleState())
        GameEventManager.notifyObservers(GameEvent.GAME_RESET)
    }
}

/**
 * GameStateManager - менеджер состояний игры
 * 
 * Управляет переходами между состояниями игры
 */
class GameStateManager {
    
    private var currentState: GameState = IdleState()
    
    /**
     * Получает текущее состояние
     */
    fun getCurrentState(): GameState = currentState
    
    /**
     * Устанавливает новое состояние
     * @param state новое состояние
     */
    fun setState(state: GameState) {
        val previousState = currentState
        currentState = state
        println("State changed: ${previousState.name} -> ${currentState.name}")
    }
    
    /**
     * Сбрасывает состояние к начальному
     */
    fun resetToIdle() {
        setState(IdleState())
    }
    
    /**
     * Получает информацию о текущем состоянии
     */
    fun getStateInfo(): String {
        return """
            Текущее состояние: ${currentState.name}
            Описание: ${currentState.description}
        """.trimIndent()
    }
    
    /**
     * Проверяет, находится ли игра в определенном состоянии
     * @param stateClass класс состояния для проверки
     * @return true если текущее состояние соответствует классу
     */
    fun isInState(stateClass: Class<out GameState>): Boolean {
        return currentState::class.java == stateClass
    }
}
