@echo off
REM Простой скрипт для сохранения вывода любой команды в файл
REM Использование: save_output.bat "команда" "имя_файла.log"

if "%1"=="" (
    echo Использование: save_output.bat "команда" [имя_файла.log]
    echo.
    echo Примеры:
    echo   save_output.bat "run.bat" run_output.log
    echo   save_output.bat "test_menu.bat" test_output.log
    echo   save_output.bat "gradlew.bat test" test_results.log
    echo.
    pause
    exit /b 1
)

set "COMMAND=%1"
set "LOG_FILE=%2"

if "%LOG_FILE%"=="" (
    set "LOG_FILE=output_%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%.log"
    set "LOG_FILE=%LOG_FILE: =0%"
)

echo.
echo 📝 Выполняю команду: %COMMAND%
echo 📄 Вывод сохраняется в: %LOG_FILE%
echo.

REM Выполняем команду и сохраняем вывод
call %COMMAND% > "%LOG_FILE%" 2>&1
set EXIT_CODE=%ERRORLEVEL%

echo.
echo ========================================
echo Команда завершена с кодом: %EXIT_CODE%
echo Лог сохранен в: %LOG_FILE%
echo ========================================
echo.
echo Хотите открыть файл лога? (Y/N)
set /p open="> "
if /i "%open%"=="Y" (
    notepad "%LOG_FILE%"
)

