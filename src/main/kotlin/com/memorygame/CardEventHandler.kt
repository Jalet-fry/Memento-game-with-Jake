package com.memorygame

/**
 * CardEventHandler - базовый класс для обработки событий карточек с паттерном Template Method
 * 
 * Применяет паттерн Template Method для определения алгоритма обработки
 * событий карточек с возможностью переопределения отдельных шагов.
 * 
 * Решает проблему: Обеспечивает единообразную обработку событий
 * с возможностью кастомизации отдельных этапов.
 */
abstract class CardEventHandler {
    
    /**
     * Основной метод обработки события карточки (Template Method)
     * @param game контекст игры
     * @param card карточка
     */
    fun handleCardEvent(game: MemoryGame, card: MemoryCard) {
        if (!canHandleEvent(game, card)) {
            return
        }
        
        // Шаг 1: Валидация
        if (!validateCard(game, card)) {
            onValidationFailed(game, card)
            return
        }
        
        // Шаг 2: Предварительная обработка
        onPreProcess(game, card)
        
        // Шаг 3: Основная обработка
        val result = processCardEvent(game, card)
        
        // Шаг 4: Постобработка
        onPostProcess(game, card, result)
        
        // Шаг 5: Уведомления
        notifyObservers(game, card, result)
    }
    
    /**
     * Проверяет, может ли обработчик обработать событие
     * @param game контекст игры
     * @param card карточка
     * @return true если событие может быть обработано
     */
    protected open fun canHandleEvent(game: MemoryGame, card: MemoryCard): Boolean {
        return !card.isMatched && card.isEnabled
    }
    
    /**
     * Валидирует карточку
     * @param game контекст игры
     * @param card карточка
     * @return true если карточка валидна
     */
    protected open fun validateCard(game: MemoryGame, card: MemoryCard): Boolean {
        return card.isEnabled && !card.isMatched
    }
    
    /**
     * Предварительная обработка
     * @param game контекст игры
     * @param card карточка
     */
    protected open fun onPreProcess(game: MemoryGame, card: MemoryCard) {
        // Базовая реализация - пустая
    }
    
    /**
     * Основная обработка события (должна быть переопределена)
     * @param game контекст игры
     * @param card карточка
     * @return результат обработки
     */
    protected abstract fun processCardEvent(game: MemoryGame, card: MemoryCard): Any?
    
    /**
     * Постобработка
     * @param game контекст игры
     * @param card карточка
     * @param result результат основной обработки
     */
    protected open fun onPostProcess(game: MemoryGame, card: MemoryCard, result: Any?) {
        // Базовая реализация - пустая
    }
    
    /**
     * Обработка ошибки валидации
     * @param game контекст игры
     * @param card карточка
     */
    protected open fun onValidationFailed(game: MemoryGame, card: MemoryCard) {
        println("Validation failed for card ${card.getCardId()}")
    }
    
    /**
     * Уведомление наблюдателей
     * @param game контекст игры
     * @param card карточка
     * @param result результат обработки
     */
    protected open fun notifyObservers(game: MemoryGame, card: MemoryCard, result: Any?) {
        // Базовая реализация - пустая
    }
}

/**
 * CardFlipHandler - обработчик переворота карточки
 */
class CardFlipHandler : CardEventHandler() {
    
    override fun onPreProcess(game: MemoryGame, card: MemoryCard) {
        println("Preparing to flip card ${card.getCardId()}")
    }
    
    override fun processCardEvent(game: MemoryGame, card: MemoryCard): Any? {
        card.flip()
        return "flipped"
    }
    
    override fun onPostProcess(game: MemoryGame, card: MemoryCard, result: Any?) {
        println("Card ${card.getCardId()} flipped successfully")
    }
    
    override fun notifyObservers(game: MemoryGame, card: MemoryCard, result: Any?) {
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
    }
}

/**
 * CardMatchHandler - обработчик совпадения карточек
 */
class CardMatchHandler : CardEventHandler() {
    
    override fun canHandleEvent(game: MemoryGame, card: MemoryCard): Boolean {
        return game.getFirstCard() != null && game.getSecondCard() != null
    }
    
    override fun validateCard(game: MemoryGame, card: MemoryCard): Boolean {
        val firstCard = game.getFirstCard()
        val secondCard = game.getSecondCard()
        return firstCard != null && secondCard != null && 
               firstCard.getCardId() == secondCard.getCardId()
    }
    
    override fun onPreProcess(game: MemoryGame, card: MemoryCard) {
        println("Processing card match for cards ${game.getFirstCard()?.getCardId()}")
    }
    
    override fun processCardEvent(game: MemoryGame, card: MemoryCard): Any? {
        val firstCard = game.getFirstCard()!!
        val secondCard = game.getSecondCard()!!
        
        firstCard.setMatched()
        secondCard.setMatched()
        
        game.incrementMatchedPairs()
        game.clearSelectedCards()
        
        return "matched"
    }
    
    override fun onPostProcess(game: MemoryGame, card: MemoryCard, result: Any?) {
        println("Cards matched successfully")
        
        // Проверяем условие победы
        val strategy = DifficultyManager.getCurrentStrategy()
        if (strategy.isGameWon(game.getMatchedPairs())) {
            game.setState(WonState())
            GameEventManager.notifyObservers(GameEvent.GAME_WON, game.getMatchedPairs())
        }
    }
    
    override fun notifyObservers(game: MemoryGame, card: MemoryCard, result: Any?) {
        GameEventManager.notifyObservers(GameEvent.CARDS_MATCHED, game.getMatchedPairs())
    }
}

/**
 * CardMismatchHandler - обработчик несовпадения карточек
 */
class CardMismatchHandler : CardEventHandler() {
    
    override fun canHandleEvent(game: MemoryGame, card: MemoryCard): Boolean {
        return game.getFirstCard() != null && game.getSecondCard() != null
    }
    
    override fun validateCard(game: MemoryGame, card: MemoryCard): Boolean {
        val firstCard = game.getFirstCard()
        val secondCard = game.getSecondCard()
        return firstCard != null && secondCard != null && 
               firstCard.getCardId() != secondCard.getCardId()
    }
    
    override fun onPreProcess(game: MemoryGame, card: MemoryCard) {
        println("Processing card mismatch")
    }
    
    override fun processCardEvent(game: MemoryGame, card: MemoryCard): Any? {
        val firstCard = game.getFirstCard()!!
        val secondCard = game.getSecondCard()!!
        
        // Задержка перед переворотом карточек обратно
        Thread.sleep(1000)
        
        firstCard.flip()
        secondCard.flip()
        
        game.clearSelectedCards()
        game.incrementAttempts()
        
        return "mismatched"
    }
    
    override fun onPostProcess(game: MemoryGame, card: MemoryCard, result: Any?) {
        println("Cards flipped back after mismatch")
    }
    
    override fun notifyObservers(game: MemoryGame, card: MemoryCard, result: Any?) {
        GameEventManager.notifyObservers(GameEvent.CARDS_MISMATCHED, game.getAttempts())
    }
}
