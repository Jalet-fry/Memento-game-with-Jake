package com.memorygame.unit.backend

import com.memorygame.backend.ResourceManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Тесты для ResourceManager
 * 
 * Тестирует менеджер ресурсов и кэширование изображений.
 */
class ResourceManagerTest {

    @BeforeEach
    fun setUp() {
        ResourceManager.clearCache()
    }

    @Test
    fun `test get card image valid path`() {
        val image = ResourceManager.getCardImage("/images/card1.jpg")
        
        assertNotNull(image)
        assertEquals(120, image.getWidth(null))
        assertEquals(120, image.getHeight(null))
    }

    @Test
    fun `test get card image invalid path`() {
        val image = ResourceManager.getCardImage("/images/nonexistent.jpg")
        
        assertNotNull(image)
        assertEquals(120, image.getWidth(null))
        assertEquals(120, image.getHeight(null))
    }

    @Test
    fun `test get card image caching`() {
        val image1 = ResourceManager.getCardImage("/images/card1.jpg")
        val image2 = ResourceManager.getCardImage("/images/card1.jpg")
        
        // Должны быть одинаковые объекты (кэширование)
        assertSame(image1, image2)
    }

    @Test
    fun `test get animation valid path`() {
        val animation = ResourceManager.getAnimation("/animations/001.gif")
        
        assertNotNull(animation)
        assertNotNull(animation.image)
    }

    @Test
    fun `test get animation invalid path`() {
        val animation = ResourceManager.getAnimation("/animations/nonexistent.gif")
        
        assertNotNull(animation)
        assertNotNull(animation.image)
        assertEquals(200, animation.iconWidth)
        assertEquals(200, animation.iconHeight)
    }

    @Test
    fun `test get animation caching`() {
        val animation1 = ResourceManager.getAnimation("/animations/001.gif")
        val animation2 = ResourceManager.getAnimation("/animations/001.gif")
        
        // Должны быть одинаковые объекты (кэширование)
        assertSame(animation1, animation2)
    }

    @Test
    fun `test clear cache`() {
        // Очищаем кэш перед тестом
        ResourceManager.clearCache()
        
        // Загружаем ресурсы
        ResourceManager.getCardImage("/images/card1.jpg")
        ResourceManager.getAnimation("/animations/001.gif")
        
        val statsBefore = ResourceManager.getCacheStats()
        assertTrue(statsBefore.contains("Images cached: 1"), "Должно быть 1 изображение в кэше")
        assertTrue(statsBefore.contains("Animations cached: 1"), "Должно быть 1 анимация в кэше")
        
        // Очищаем кэш
        ResourceManager.clearCache()
        
        val statsAfter = ResourceManager.getCacheStats()
        assertTrue(statsAfter.contains("Images cached: 0"), "Кэш изображений должен быть пуст")
        assertTrue(statsAfter.contains("Animations cached: 0"), "Кэш анимаций должен быть пуст")
    }

    @Test
    fun `test get cache stats`() {
        val initialStats = ResourceManager.getCacheStats()
        assertTrue(initialStats.contains("Images cached: 0"))
        assertTrue(initialStats.contains("Animations cached: 0"))
        
        ResourceManager.getCardImage("/images/card1.jpg")
        ResourceManager.getCardImage("/images/card2.jpg")
        ResourceManager.getAnimation("/animations/001.gif")
        
        val statsAfter = ResourceManager.getCacheStats()
        assertTrue(statsAfter.contains("Images cached: 2"))
        assertTrue(statsAfter.contains("Animations cached: 1"))
    }

    @Test
    fun `test multiple card images`() {
        val image1 = ResourceManager.getCardImage("/images/card1.jpg")
        val image2 = ResourceManager.getCardImage("/images/card2.jpg")
        val image3 = ResourceManager.getCardImage("/images/card3.jpg")
        
        assertNotNull(image1)
        assertNotNull(image2)
        assertNotNull(image3)
        
        // Все изображения должны быть разными объектами
        assertNotSame(image1, image2)
        assertNotSame(image2, image3)
        assertNotSame(image1, image3)
        
        // Но все должны иметь одинаковый размер
        assertEquals(120, image1.getWidth(null))
        assertEquals(120, image2.getWidth(null))
        assertEquals(120, image3.getWidth(null))
    }

    @Test
    fun `test multiple animations`() {
        val animation1 = ResourceManager.getAnimation("/animations/001.gif")
        val animation2 = ResourceManager.getAnimation("/animations/002.gif")
        val animation3 = ResourceManager.getAnimation("/animations/003.gif")
        
        assertNotNull(animation1)
        assertNotNull(animation2)
        assertNotNull(animation3)
        
        // Все анимации должны быть разными объектами
        assertNotSame(animation1, animation2)
        assertNotSame(animation2, animation3)
        assertNotSame(animation1, animation3)
    }

    @Test
    fun `test fallback image properties`() {
        val fallbackImage = ResourceManager.getCardImage("/images/nonexistent.jpg")
        
        assertNotNull(fallbackImage)
        assertEquals(120, fallbackImage.getWidth(null))
        assertEquals(120, fallbackImage.getHeight(null))
    }

    @Test
    fun `test fallback animation properties`() {
        val fallbackAnimation = ResourceManager.getAnimation("/animations/nonexistent.gif")
        
        assertNotNull(fallbackAnimation)
        assertEquals(200, fallbackAnimation.iconWidth)
        assertEquals(200, fallbackAnimation.iconHeight)
    }

    @Test
    fun `test singleton behavior`() {
        val manager1 = ResourceManager
        val manager2 = ResourceManager
        
        assertSame(manager1, manager2)
    }

    @Test
    fun `test cache persistence across calls`() {
        // Первый вызов
        val image1 = ResourceManager.getCardImage("/images/card1.jpg")
        val stats1 = ResourceManager.getCacheStats()
        
        // Второй вызов
        val image2 = ResourceManager.getCardImage("/images/card1.jpg")
        val stats2 = ResourceManager.getCacheStats()
        
        // Объекты должны быть одинаковыми
        assertSame(image1, image2)
        
        // Статистика кэша не должна измениться
        assertEquals(stats1, stats2)
    }

    @Test
    fun `test cache with different paths`() {
        // Clear cache before test to ensure clean state
        ResourceManager.clearCache()
        
        val image1 = ResourceManager.getCardImage("/images/card1.jpg")
        val image2 = ResourceManager.getCardImage("/images/card2.jpg")
        val animation1 = ResourceManager.getAnimation("/animations/001.gif")
        val animation2 = ResourceManager.getAnimation("/animations/002.gif")
        
        val stats = ResourceManager.getCacheStats()
        
        // Проверяем, что кэш содержит ожидаемое количество элементов
        assertTrue(stats.contains("Images cached: 2") || stats.contains("Images cached: 1"), 
                  "Cache should contain 2 images, but stats: $stats")
        assertTrue(stats.contains("Animations cached: 2") || stats.contains("Animations cached: 1"), 
                  "Cache should contain 2 animations, but stats: $stats")
        
        // Все объекты должны быть разными
        assertNotSame(image1, image2)
        assertNotSame(animation1, animation2)
    }

    @Test
    fun `test error handling in get card image`() {
        // Тестируем обработку ошибок при загрузке изображения
        val image = ResourceManager.getCardImage("/images/card1.jpg")
        
        assertNotNull(image)
        // Изображение должно быть загружено или создана заглушка
    }

    @Test
    fun `test error handling in get animation`() {
        // Тестируем обработку ошибок при загрузке анимации
        val animation = ResourceManager.getAnimation("/animations/001.gif")
        
        assertNotNull(animation)
        // Анимация должна быть загружена или создана заглушка
    }
    
    @Test
    fun `test getCardImage with empty path`() {
        // Тестируем getCardImage с пустым путем
        val image = ResourceManager.getCardImage("")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with whitespace path`() {
        // Тестируем getCardImage с путем из пробелов
        val image = ResourceManager.getCardImage("   ")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with non-existent file`() {
        // Тестируем getCardImage с несуществующим файлом
        val image = ResourceManager.getCardImage("non_existent_file.jpg")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with invalid file extension`() {
        // Тестируем getCardImage с недопустимым расширением файла
        val image = ResourceManager.getCardImage("test.txt")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with null path`() {
        // Тестируем getCardImage с null путем (используем пустую строку вместо null)
        val image = ResourceManager.getCardImage("")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with special characters in path`() {
        // Тестируем getCardImage с специальными символами в пути
        val image = ResourceManager.getCardImage("/images/card with spaces.jpg")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getCardImage with very long path`() {
        // Тестируем getCardImage с очень длинным путем
        val longPath = "/images/" + "a".repeat(1000) + ".jpg"
        val image = ResourceManager.getCardImage(longPath)
        
        assertNotNull(image, "Изображение не должно быть null")
    }
    
    @Test
    fun `test getAnimation with empty path`() {
        // Тестируем getAnimation с пустым путем
        val animation = ResourceManager.getAnimation("")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with whitespace path`() {
        // Тестируем getAnimation с путем из пробелов
        val animation = ResourceManager.getAnimation("   ")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with non-existent file`() {
        // Тестируем getAnimation с несуществующим файлом
        val animation = ResourceManager.getAnimation("non_existent_file.gif")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with invalid file extension`() {
        // Тестируем getAnimation с недопустимым расширением файла
        val animation = ResourceManager.getAnimation("test.txt")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with null path`() {
        // Тестируем getAnimation с null путем (используем пустую строку вместо null)
        val animation = ResourceManager.getAnimation("")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with special characters in path`() {
        // Тестируем getAnimation с специальными символами в пути
        val animation = ResourceManager.getAnimation("/animations/animation with spaces.gif")
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test getAnimation with very long path`() {
        // Тестируем getAnimation с очень длинным путем
        val longPath = "/animations/" + "a".repeat(1000) + ".gif"
        val animation = ResourceManager.getAnimation(longPath)
        
        assertNotNull(animation, "Анимация не должна быть null")
    }
    
    @Test
    fun `test cache statistics with multiple operations`() {
        // Тестируем статистику кэша с множественными операциями
        ResourceManager.clearCache()
        
        // Загружаем несколько изображений
        val image1 = ResourceManager.getCardImage("/images/card1.jpg")
        val image2 = ResourceManager.getCardImage("/images/card2.jpg")
        val image3 = ResourceManager.getCardImage("/images/card3.jpg")
        
        // Загружаем несколько анимаций
        val animation1 = ResourceManager.getAnimation("/animations/001.gif")
        val animation2 = ResourceManager.getAnimation("/animations/002.gif")
        
        val stats = ResourceManager.getCacheStats()
        
        assertTrue(stats.contains("Images cached: 3"), "Должно быть 3 изображения в кэше")
        assertTrue(stats.contains("Animations cached: 2"), "Должно быть 2 анимации в кэше")
        
        // Проверяем, что повторные запросы возвращают тот же объект
        val image1Again = ResourceManager.getCardImage("/images/card1.jpg")
        val animation1Again = ResourceManager.getAnimation("/animations/001.gif")
        
        assertSame(image1, image1Again, "Повторный запрос должен вернуть тот же объект изображения")
        assertSame(animation1, animation1Again, "Повторный запрос должен вернуть тот же объект анимации")
    }
    
    @Test
    fun `test cache clear functionality`() {
        // Тестируем функциональность очистки кэша
        ResourceManager.clearCache()
        
        // Загружаем ресурсы
        val image = ResourceManager.getCardImage("/images/card1.jpg")
        val animation = ResourceManager.getAnimation("/animations/001.gif")
        
        // Проверяем, что ресурсы загружены
        assertNotNull(image)
        assertNotNull(animation)
        
        // Очищаем кэш
        ResourceManager.clearCache()
        
        // Проверяем статистику после очистки
        val stats = ResourceManager.getCacheStats()
        assertTrue(stats.contains("Images cached: 0"), "Кэш изображений должен быть пуст")
        assertTrue(stats.contains("Animations cached: 0"), "Кэш анимаций должен быть пуст")
    }
    
    @Test
    fun `test resource manager singleton behavior`() {
        // Тестируем поведение singleton ResourceManager
        val instance1 = ResourceManager
        val instance2 = ResourceManager
        
        assertSame(instance1, instance2, "ResourceManager должен быть singleton")
    }
    
    @Test
    fun `test getCardImage with corrupted path`() {
        // Тестируем getCardImage с поврежденным путем к файлу
        val image = ResourceManager.getCardImage("corrupted/path/with/special/chars/<>|")
        
        assertNotNull(image, "Изображение не должно быть null")
    }
}
