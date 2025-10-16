@echo off
chcp 65001 >nul
title Memory Game with Jake - Launcher

REM Получаем текущую директорию
set "CURRENT_DIR=%~dp0"

echo =======================================
echo   🎮 MEMORY GAME WITH JAKE 🎮
echo =======================================
echo.

REM Проверяем, существует ли готовый JAR файл
if exist "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar" (
    echo ✅ Найден готовый файл игры!
    echo 🚀 Запуск игры...
    echo.
    
    REM Запускаем готовое приложение
    java -jar "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar"
    goto :end
)

REM Если JAR не найден, предлагаем варианты
echo ❌ Готовый файл игры не найден!
echo.
echo Выберите действие:
echo [1] Собрать и запустить игру
echo [2] Собрать только (без запуска)
echo [3] Выход
echo.
set /p choice="Введите номер (1-3): "

if "%choice%"=="1" (
    echo.
    echo 🔨 Выполняется сборка...
    gradlew.bat fatJar
    
    echo.
    if exist "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar" (
        echo ✅ Сборка завершена успешно!
        echo 🚀 Запуск игры...
        echo.
        java -jar "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar"
    ) else (
        echo ❌ Ошибка сборки! Проверьте наличие Java и Gradle.
        pause
    )
) else if "%choice%"=="2" (
    echo.
    echo 🔨 Выполняется сборка...
    gradlew.bat fatJar
    
    echo.
    if exist "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar" (
        echo ✅ Сборка завершена успешно!
        echo 📁 Готовый файл: build\libs\MemoryGame-1.0.jar
        echo 🖱️  Дважды кликните по файлу для запуска!
        echo.
        echo 📎 Хотите создать ярлык? (Y/N)
        set /p shortcut="Введите выбор: "
        if /i "%shortcut%"=="Y" (
            call create_shortcut.bat
        )
    ) else (
        echo ❌ Ошибка сборки! Проверьте наличие Java и Gradle.
        pause
    )
) else if "%choice%"=="3" (
    echo До свидания! 👋
    exit /b
) else (
    echo ❌ Неверный выбор!
    pause
)

:end
echo.
echo =======================================
echo   Игра завершена!
echo =======================================
pause
