@echo off
chcp 65001 >nul
echo =======================================
echo   📎 СОЗДАНИЕ ЯРЛЫКА ДЛЯ ИГРЫ 📎
echo =======================================
echo.

REM Получаем текущую директорию
set "CURRENT_DIR=%~dp0"

REM Проверяем, существует ли JAR файл
if exist "%CURRENT_DIR%build\libs\MemoryGame-1.0.jar" (
    echo ✅ Найден файл игры!
    echo 📎 Создание ярлыка...
    
    REM Создаем ярлык с помощью PowerShell
    powershell -Command "$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut('%CURRENT_DIR%Memory Game with Jake.lnk'); $Shortcut.TargetPath = 'java'; $Shortcut.Arguments = '-jar \"%CURRENT_DIR%build\libs\MemoryGame-1.0.jar\"'; $Shortcut.WorkingDirectory = '%CURRENT_DIR%'; $Shortcut.Description = 'Memory Game with Jake - Игра Мементо с анимациями'; $Shortcut.IconLocation = 'java.exe,0'; $Shortcut.Save()"
    
    echo ✅ Ярлык создан: "Memory Game with Jake.lnk"
    echo.
    echo 🎮 Теперь вы можете:
    echo    • Дважды кликнуть по ярлыку для запуска игры
    echo    • Перетащить ярлык на рабочий стол
    echo    • Закрепить ярлык в меню Пуск
    echo.
) else (
    echo ❌ Файл игры не найден!
    echo 🔨 Сначала выполните сборку: build.bat
    echo.
)

pause
