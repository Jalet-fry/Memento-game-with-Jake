import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

val sourceDir = File("Animation")
val targetDir = File("src/main/resources/animations")

// Создаем целевую директорию
targetDir.mkdirs()

// Копируем все .gif файлы
sourceDir.listFiles()?.filter { it.extension == "gif" }?.forEach { file ->
    val target = File(targetDir, file.name)
    Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    println("Copied: ${file.name}")
}

println("Done! Copied ${targetDir.listFiles()?.size ?: 0} files")

