package com.memorygame

/**
 * GameEvent - перечисление событий игры
 */
enum class GameEvent {
    CARD_FLIPPED,
    CARDS_MATCHED,
    CARDS_MISMATCHED,
    GAME_WON,
    GAME_RESET,
    SETTINGS_CHANGED,
    ACHIEVEMENT_UNLOCKED
}

/**
 * GameObserver - интерфейс для наблюдателей событий игры
 * 
 * Применяет паттерн Observer для уведомления компонентов
 * о событиях игры.
 * 
 * Решает проблему: Разделяет игровую логику и UI,
 * позволяя компонентам реагировать на события независимо.
 */
interface GameObserver {
    /**
     * Обрабатывает событие игры
     * @param event тип события
     * @param data дополнительные данные события
     */
    fun onGameEvent(event: GameEvent, data: Any? = null)
}

/**
 * GameEventManager - менеджер событий игры с паттерном Observer
 * 
 * Применяет паттерн Observer для управления подписками
 * и уведомлениями о событиях игры.
 * 
 * Решает проблему: Централизованное управление событиями
 * и слабая связанность между компонентами.
 */
object GameEventManager {
    
    private val observers = mutableListOf<GameObserver>()
    
    /**
     * Подписывает наблюдателя на события
     * @param observer наблюдатель для подписки
     */
    fun subscribe(observer: GameObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }
    
    /**
     * Отписывает наблюдателя от событий
     * @param observer наблюдатель для отписки
     */
    fun unsubscribe(observer: GameObserver) {
        observers.remove(observer)
    }
    
    /**
     * Уведомляет всех наблюдателей о событии
     * @param event событие для уведомления
     * @param data дополнительные данные
     */
    fun notifyObservers(event: GameEvent, data: Any? = null) {
        observers.forEach { observer ->
            try {
                observer.onGameEvent(event, data)
            } catch (e: Exception) {
                // Логируем ошибку, но не прерываем уведомление других наблюдателей
                println("Error notifying observer: ${e.message}")
            }
        }
    }
    
    /**
     * Очищает всех наблюдателей
     */
    fun clearObservers() {
        observers.clear()
    }
    
    /**
     * Получает количество подписанных наблюдателей
     */
    fun getObserverCount(): Int = observers.size
}

/**
 * UIEventObserver - базовый наблюдатель для UI компонентов
 * 
 * Предоставляет базовую реализацию GameObserver для UI компонентов
 */
abstract class UIEventObserver : GameObserver {
    
    override fun onGameEvent(event: GameEvent, data: Any?) {
        when (event) {
            GameEvent.CARD_FLIPPED -> onCardFlipped(data)
            GameEvent.CARDS_MATCHED -> onCardsMatched(data)
            GameEvent.CARDS_MISMATCHED -> onCardsMismatched(data)
            GameEvent.GAME_WON -> onGameWon(data)
            GameEvent.GAME_RESET -> onGameReset(data)
            GameEvent.SETTINGS_CHANGED -> onSettingsChanged(data)
            GameEvent.ACHIEVEMENT_UNLOCKED -> onAchievementUnlocked(data)
        }
    }
    
    protected open fun onCardFlipped(data: Any?) {}
    protected open fun onCardsMatched(data: Any?) {}
    protected open fun onCardsMismatched(data: Any?) {}
    protected open fun onGameWon(data: Any?) {}
    protected open fun onGameReset(data: Any?) {}
    protected open fun onSettingsChanged(data: Any?) {}
    protected open fun onAchievementUnlocked(data: Any?) {}
}
