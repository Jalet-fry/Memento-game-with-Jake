# 🎯 Примененные паттерны проектирования в игре "Мементо с Jake"

## Обзор

В рамках лабораторной работы №5 "Реализация учебного проекта" в игру "Мементо с Jake" были применены следующие паттерны проектирования:

1. **Singleton** - для управления ресурсами и настройками
2. **Observer** - для системы событий игры
3. **Strategy** - для различных уровней сложности
4. **Factory** - для создания карточек
5. **State** - для управления состояниями игры
6. **Template Method** - для обработки событий карточек

---

## 1️⃣ Паттерн Singleton

### Применение
Паттерн Singleton применен в следующих классах:

#### ResourceManager (`src/main/kotlin/com/memorygame/ResourceManager.kt`)
```kotlin
object ResourceManager {
    private val imageCache = mutableMapOf<String, Image>()
    private val animationCache = mutableMapOf<String, ImageIcon>()
    
    fun getCardImage(path: String): Image { ... }
    fun getAnimation(path: String): ImageIcon { ... }
}
```

#### SettingsManager (`src/main/kotlin/com/memorygame/SettingsManager.kt`)
```kotlin
object SettingsManager {
    private var _animationsEnabled = true
    private var _soundEnabled = true
    private var _currentTheme = "dark"
    private var _difficulty = 4
    
    fun toggleAnimations() { ... }
    fun setTheme(theme: String) { ... }
}
```

#### GameEventManager (`src/main/kotlin/com/memorygame/GameObserver.kt`)
```kotlin
object GameEventManager {
    private val observers = mutableListOf<GameObserver>()
    
    fun subscribe(observer: GameObserver) { ... }
    fun notifyObservers(event: GameEvent, data: Any?) { ... }
}
```

#### DifficultyManager (`src/main/kotlin/com/memorygame/DifficultyStrategy.kt`)
```kotlin
object DifficultyManager {
    private val strategies = mapOf(
        4 to EasyDifficultyStrategy(),
        6 to MediumDifficultyStrategy(),
        8 to HardDifficultyStrategy()
    )
    
    fun getCurrentStrategy(): DifficultyStrategy { ... }
}
```

### Решаемые проблемы
- **Единая точка доступа**: Обеспечивает единственный экземпляр для управления ресурсами и настройками
- **Глобальное состояние**: Позволяет различным компонентам обращаться к общим данным
- **Кэширование**: ResourceManager кэширует загруженные изображения и анимации
- **Консистентность**: SettingsManager обеспечивает синхронизацию настроек между компонентами

### Преимущества
- ✅ Экономия памяти (единственный экземпляр)
- ✅ Глобальный доступ к ресурсам
- ✅ Централизованное управление состоянием
- ✅ Простота использования

---

## 2️⃣ Паттерн Observer

### Применение
Паттерн Observer реализован в системе событий игры:

#### Интерфейс GameObserver (`src/main/kotlin/com/memorygame/GameObserver.kt`)
```kotlin
interface GameObserver {
    fun onGameEvent(event: GameEvent, data: Any? = null)
}

enum class GameEvent {
    CARD_FLIPPED, CARDS_MATCHED, CARDS_MISMATCHED,
    GAME_WON, GAME_RESET, SETTINGS_CHANGED, ACHIEVEMENT_UNLOCKED
}
```

#### Базовый класс UIEventObserver
```kotlin
abstract class UIEventObserver : GameObserver {
    override fun onGameEvent(event: GameEvent, data: Any?) {
        when (event) {
            GameEvent.CARD_FLIPPED -> onCardFlipped(data)
            GameEvent.CARDS_MATCHED -> onCardsMatched(data)
            // ... другие события
        }
    }
}
```

#### Использование в MemoryGame
```kotlin
class MemoryGame : JFrame("Игра Мементо"), GameObserver {
    init {
        GameEventManager.subscribe(this)
    }
    
    override fun onGameEvent(event: GameEvent, data: Any?) {
        when (event) {
            GameEvent.CARD_FLIPPED -> playSound("flip")
            GameEvent.CARDS_MATCHED -> showAnimation("match")
            // ... обработка других событий
        }
    }
}
```

### Решаемые проблемы
- **Слабая связанность**: Компоненты не знают друг о друге напрямую
- **Расширяемость**: Легко добавлять новых наблюдателей
- **Уведомления**: Автоматическое уведомление всех заинтересованных компонентов
- **Разделение ответственности**: Логика игры отделена от UI

### Преимущества
- ✅ Слабая связанность компонентов
- ✅ Легкое добавление новых наблюдателей
- ✅ Автоматические уведомления
- ✅ Гибкая архитектура

---

## 3️⃣ Паттерн Strategy

### Применение
Паттерн Strategy применен для различных уровней сложности игры:

#### Интерфейс DifficultyStrategy (`src/main/kotlin/com/memorygame/DifficultyStrategy.kt`)
```kotlin
interface DifficultyStrategy {
    val gridSize: Int
    val totalPairs: Int
    val name: String
    val description: String
    val timeLimit: Int
    val maxAttempts: Int
    
    fun isGameWon(matchedPairs: Int): Boolean
    fun getGameRating(time: Int, attempts: Int): Int
}
```

#### Конкретные стратегии
```kotlin
class EasyDifficultyStrategy : DifficultyStrategy {
    override val gridSize: Int = 4
    override val totalPairs: Int = 8
    override val name: String = "Легко"
    
    override fun getGameRating(time: Int, attempts: Int): Int {
        return when {
            time <= 30 && attempts <= 8 -> 5
            time <= 60 && attempts <= 12 -> 4
            // ... другие условия
        }
    }
}

class MediumDifficultyStrategy : DifficultyStrategy { ... }
class HardDifficultyStrategy : DifficultyStrategy { ... }
```

#### Использование в игре
```kotlin
// В MemoryGame
val strategy = DifficultyManager.getCurrentStrategy()
val cardSet = CardFactory.createCardSet(strategy.gridSize, imagePaths)

// Проверка победы
if (strategy.isGameWon(matchedPairs)) {
    val rating = strategy.getGameRating(elapsedSeconds, attempts)
    // Показать рейтинг
}
```

### Решаемые проблемы
- **Алгоритмы сложности**: Различные уровни сложности с разными правилами
- **Расширяемость**: Легко добавлять новые уровни сложности
- **Инкапсуляция**: Логика каждого уровня изолирована
- **Переключение**: Динамическое изменение стратегии во время игры

### Преимущества
- ✅ Легкое добавление новых уровней сложности
- ✅ Инкапсуляция алгоритмов
- ✅ Динамическое переключение стратегий
- ✅ Чистый код без условных операторов

---

## 4️⃣ Паттерн Factory

### Применение
Паттерн Factory применен для создания карточек игры:

#### CardFactory (`src/main/kotlin/com/memorygame/CardFactory.kt`)
```kotlin
object CardFactory {
    fun createCard(cardId: Int, imagePath: String): MemoryCard {
        return MemoryCard(cardId, imagePath)
    }
    
    fun createCardPair(pairId: Int, imagePath: String): List<MemoryCard> {
        return listOf(
            createCard(pairId, imagePath),
            createCard(pairId, imagePath)
        )
    }
    
    fun createCardSet(gridSize: Int, imagePaths: List<String>): List<MemoryCard> {
        val totalPairs = (gridSize * gridSize) / 2
        val cards = mutableListOf<MemoryCard>()
        
        for (i in 0 until totalPairs) {
            val imagePath = imagePaths[i % imagePaths.size]
            val pair = createCardPair(i, imagePath)
            cards.addAll(pair)
        }
        
        cards.shuffle()
        return cards
    }
}
```

#### Использование в MemoryGame
```kotlin
// Создание полного набора карточек
val cardSet = CardFactory.createCardSet(strategy.gridSize, imagePaths)

// Создание отдельной карточки
val card = CardFactory.createCard(1, "/images/card1.jpg")

// Создание пары карточек
val pair = CardFactory.createCardPair(1, "/images/card1.jpg")
```

### Решаемые проблемы
- **Централизованное создание**: Единая точка создания карточек
- **Инкапсуляция логики**: Скрытие сложности создания объектов
- **Консистентность**: Единообразное создание карточек
- **Валидация**: Проверка параметров при создании

### Преимущества
- ✅ Централизованная логика создания
- ✅ Инкапсуляция сложности
- ✅ Единообразное создание объектов
- ✅ Легкое тестирование

---

## 5️⃣ Паттерн State

### Применение
Паттерн State применен для управления состояниями игры:

#### Интерфейс GameState (`src/main/kotlin/com/memorygame/GameState.kt`)
```kotlin
interface GameState {
    fun handleCardClick(game: MemoryGame, card: MemoryCard)
    fun handleGameReset(game: MemoryGame)
    val name: String
    val description: String
}
```

#### Конкретные состояния
```kotlin
class IdleState : GameState {
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        if (!card.isFlipped && !card.isMatched) {
            card.flip()
            game.setFirstCard(card)
            game.setState(PlayingState())
            GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
        }
    }
}

class PlayingState : GameState {
    override fun handleCardClick(game: MemoryGame, card: MemoryCard) {
        if (!card.isFlipped && !card.isMatched && card != game.getFirstCard()) {
            card.flip()
            game.setSecondCard(card)
            game.setState(CheckingState())
        }
    }
}

class CheckingState : GameState { ... }
class WonState : GameState { ... }
```

#### GameStateManager
```kotlin
class GameStateManager {
    private var currentState: GameState = IdleState()
    
    fun setState(state: GameState) {
        val previousState = currentState
        currentState = state
        println("State changed: ${previousState.name} -> ${currentState.name}")
    }
    
    fun getCurrentState(): GameState = currentState
}
```

### Решаемые проблемы
- **Управление состояниями**: Четкое разделение поведения по состояниям
- **Переходы состояний**: Контролируемые переходы между состояниями
- **Инкапсуляция**: Поведение каждого состояния изолировано
- **Расширяемость**: Легко добавлять новые состояния

### Преимущества
- ✅ Четкое разделение поведения
- ✅ Контролируемые переходы состояний
- ✅ Легкое добавление новых состояний
- ✅ Инкапсуляция логики состояний

---

## 6️⃣ Паттерн Template Method

### Применение
Паттерн Template Method применен для обработки событий карточек:

#### Базовый класс CardEventHandler (`src/main/kotlin/com/memorygame/CardEventHandler.kt`)
```kotlin
abstract class CardEventHandler {
    fun handleCardEvent(game: MemoryGame, card: MemoryCard) {
        if (!canHandleEvent(game, card)) return
        
        if (!validateCard(game, card)) {
            onValidationFailed(game, card)
            return
        }
        
        onPreProcess(game, card)
        val result = processCardEvent(game, card)
        onPostProcess(game, card, result)
        notifyObservers(game, card, result)
    }
    
    protected open fun canHandleEvent(game: MemoryGame, card: MemoryCard): Boolean
    protected open fun validateCard(game: MemoryGame, card: MemoryCard): Boolean
    protected abstract fun processCardEvent(game: MemoryGame, card: MemoryCard): Any?
    protected open fun onPreProcess(game: MemoryGame, card: MemoryCard) { }
    protected open fun onPostProcess(game: MemoryGame, card: MemoryCard, result: Any?) { }
}
```

#### Конкретные обработчики
```kotlin
class CardFlipHandler : CardEventHandler() {
    override fun processCardEvent(game: MemoryGame, card: MemoryCard): Any? {
        card.flip()
        return "flipped"
    }
    
    override fun notifyObservers(game: MemoryGame, card: MemoryCard, result: Any?) {
        GameEventManager.notifyObservers(GameEvent.CARD_FLIPPED, card)
    }
}

class CardMatchHandler : CardEventHandler() {
    override fun processCardEvent(game: MemoryGame, card: MemoryCard): Any? {
        val firstCard = game.getFirstCard()!!
        val secondCard = game.getSecondCard()!!
        
        firstCard.setMatched()
        secondCard.setMatched()
        game.incrementMatchedPairs()
        game.clearSelectedCards()
        
        return "matched"
    }
}
```

### Решаемые проблемы
- **Единообразная обработка**: Общий алгоритм обработки событий
- **Кастомизация**: Возможность переопределения отдельных шагов
- **Повторное использование**: Общая логика для всех обработчиков
- **Расширяемость**: Легко добавлять новые типы обработчиков

### Преимущества
- ✅ Единообразный алгоритм обработки
- ✅ Возможность кастомизации шагов
- ✅ Повторное использование кода
- ✅ Легкое добавление новых обработчиков

---

## 📊 Итоговая архитектура

### Диаграмма взаимодействия паттернов

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MemoryGame    │    │  ResourceManager│    │ SettingsManager │
│   (Observer)    │◄───┤   (Singleton)   │    │   (Singleton)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ GameEventManager│    │   CardFactory   │    │DifficultyManager│
│   (Singleton)   │    │   (Factory)     │    │   (Singleton)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ GameStateManager│    │CardEventHandler │    │DifficultyStrategy│
│     (State)     │    │(Template Method)│    │   (Strategy)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Преимущества применения паттернов

1. **Модульность**: Каждый паттерн решает конкретную задачу
2. **Расширяемость**: Легко добавлять новые функции
3. **Тестируемость**: Компоненты можно тестировать изолированно
4. **Поддерживаемость**: Код легче понимать и модифицировать
5. **Переиспользование**: Компоненты можно использовать в других проектах

### Соответствие принципам SOLID

- **S (Single Responsibility)**: Каждый класс имеет одну ответственность
- **O (Open/Closed)**: Классы открыты для расширения, закрыты для модификации
- **L (Liskov Substitution)**: Подклассы могут заменять базовые классы
- **I (Interface Segregation)**: Интерфейсы разделены по функциональности
- **D (Dependency Inversion)**: Зависимости направлены на абстракции

---

## 🎯 Заключение

Применение паттернов проектирования значительно улучшило архитектуру игры "Мементо с Jake":

- **Код стал более структурированным** и понятным
- **Упростилось добавление новых функций** (новые уровни сложности, состояния, обработчики)
- **Улучшилась тестируемость** благодаря слабой связанности компонентов
- **Повысилась переиспользуемость** кода

Все паттерны работают совместно, создавая гибкую и расширяемую архитектуру, которая соответствует принципам объектно-ориентированного программирования и лучшим практикам разработки ПО.

---

*Документация создана для лабораторной работы №5 "Реализация учебного проекта"*
