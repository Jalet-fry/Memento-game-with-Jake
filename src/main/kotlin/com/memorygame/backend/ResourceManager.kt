package com.memorygame.backend

import java.awt.Image
import javax.swing.ImageIcon
import java.io.InputStream

/**
 * ResourceManager - Singleton для управления ресурсами игры
 * 
 * Применяет паттерн Singleton для централизованного управления
 * изображениями и анимациями игры.
 * 
 * Решает проблему: Избегает дублирования загрузки ресурсов
 * и обеспечивает единую точку доступа к ним.
 */
object ResourceManager {
    
    private val imageCache = mutableMapOf<String, Image>()
    private val animationCache = mutableMapOf<String, ImageIcon>()
    
    /**
     * Получает изображение карточки по пути
     * @param path путь к изображению
     * @return Image объект изображения
     */
    fun getCardImage(path: String): Image {
        return imageCache.getOrPut(path) {
            try {
                val inputStream: InputStream? = ResourceManager::class.java.getResourceAsStream(path)
                if (inputStream != null) {
                    javax.imageio.ImageIO.read(inputStream)?.getScaledInstance(120, 120, Image.SCALE_SMOOTH)
                        ?: createFallbackImage()
                } else {
                    createFallbackImage()
                }
            } catch (e: Exception) {
                createFallbackImage()
            }
        }
    }
    
    /**
     * Получает анимацию по пути
     * @param path путь к анимации
     * @return ImageIcon объект анимации
     */
    fun getAnimation(path: String): ImageIcon {
        return animationCache.getOrPut(path) {
            try {
                val resourceUrl = ResourceManager::class.java.getResource(path)
                if (resourceUrl != null) {
                    ImageIcon(resourceUrl)
                } else {
                    createFallbackAnimation()
                }
            } catch (e: Exception) {
                createFallbackAnimation()
            }
        }
    }
    
    /**
     * Создает заглушку для изображения карточки
     */
    private fun createFallbackImage(): Image {
        val img = java.awt.image.BufferedImage(120, 120, java.awt.image.BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()
        g.color = java.awt.Color(100, 100, 100)
        g.fillRect(0, 0, 120, 120)
        g.color = java.awt.Color.WHITE
        g.font = java.awt.Font("Arial", java.awt.Font.BOLD, 16)
        g.drawString("?", 50, 60)
        g.dispose()
        return img
    }
    
    /**
     * Создает заглушку для анимации
     */
    private fun createFallbackAnimation(): ImageIcon {
        val img = java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()
        g.color = java.awt.Color(50, 50, 50)
        g.fillRect(0, 0, 200, 200)
        g.color = java.awt.Color.WHITE
        g.font = java.awt.Font("Arial", java.awt.Font.BOLD, 24)
        g.drawString("Jake", 70, 100)
        g.dispose()
        return ImageIcon(img)
    }
    
    /**
     * Очищает кэш ресурсов
     */
    fun clearCache() {
        imageCache.clear()
        animationCache.clear()
    }
    
    /**
     * Получает статистику кэша
     */
    fun getCacheStats(): String {
        return "Images cached: ${imageCache.size}, Animations cached: ${animationCache.size}"
    }
}
