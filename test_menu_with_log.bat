@echo off
chcp 65001 >nul

REM Создаем имя файла лога с временной меткой
set "LOG_FILE=test_output_%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%.log"
set "LOG_FILE=%LOG_FILE: =0%"

echo.
echo  ████████╗███████╗███████╗████████╗
echo  ╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝
echo     ██║   █████╗  ███████╗   ██║
echo     ██║   ██╔══╝  ╚════██║   ██║
echo     ██║   ███████╗███████║   ██║
echo     ╚═╝   ╚══════╝╚══════╝   ╚═╝
echo.
echo  ██████╗ ██████╗ ██╗   ██╗███████╗
echo ██╔════╝██╔═══██╗██║   ██║██╔════╝
echo ██║     ██║   ██║██║   ██║█████╗
echo ██║     ██║   ██║╚██╗ ██╔╝██╔══╝
echo ╚██████╗╚██████╔╝ ╚████╔╝ ███████╗
echo  ╚═════╝ ╚═════╝   ╚═══╝  ╚══════╝
echo.
echo [QUICK TESTING LAUNCH WITH LOGGING]
echo.
echo 📝 Вывод сохраняется в файл: %LOG_FILE%
echo.
echo Choose action:
echo 1. Run all tests with JaCoCo coverage
echo 2. Run only successful tests
echo 3. Open JaCoCo HTML report (if exists)
echo 4. Create detailed report (PowerShell)
echo 5. Exit
echo.
set /p choice="Enter number (1-5): "

REM Сохраняем выбор пользователя
set "USER_CHOICE=%choice%"

REM Перенаправляем вывод в файл
(
echo [TEST MENU OUTPUT LOG]
echo Date: %date% %time%
echo User choice: %USER_CHOICE%
echo.

if "%USER_CHOICE%"=="1" goto run_all_tests
if "%USER_CHOICE%"=="2" goto run_successful_tests
if "%USER_CHOICE%"=="3" goto open_jacoco_report
if "%USER_CHOICE%"=="4" goto run_powershell
if "%USER_CHOICE%"=="5" goto end
goto invalid_choice

:run_all_tests
echo.
echo ========================================
echo [RUNNING ALL TESTS WITH JACOCO...]
echo Start time: %date% %time%
echo ========================================
echo.
echo Executing: .\gradlew.bat clean test --rerun-tasks jacocoTestReport openJacocoReport
echo.
call .\gradlew.bat clean test --rerun-tasks jacocoTestReport openJacocoReport
set TEST_EXIT_CODE=%ERRORLEVEL%
echo.
echo ========================================
echo Test execution completed
echo Exit code: %TEST_EXIT_CODE%
echo End time: %date% %time%
echo ========================================
echo.
if %TEST_EXIT_CODE% equ 0 (
    echo [TESTS COMPLETED SUCCESSFULLY]
    echo JaCoCo HTML report should have opened automatically!
) else (
    echo [TESTS FAILED WITH EXIT CODE: %TEST_EXIT_CODE%]
    echo Please check the output above for details.
)
goto show_results

:run_successful_tests
echo.
echo ========================================
echo [RUNNING SUCCESSFUL TESTS ONLY...]
echo Start time: %date% %time%
echo ========================================
echo.
echo Executing: .\gradlew.bat test --continue
echo.
call .\gradlew.bat test --continue
set TEST_EXIT_CODE=%ERRORLEVEL%
echo.
echo ========================================
echo Test execution completed
echo Exit code: %TEST_EXIT_CODE%
echo End time: %date% %time%
echo ========================================
goto show_results

:open_jacoco_report
echo.
echo [OPENING JACOCO HTML REPORT...]
if exist "build\reports\jacoco\test\html\index.html" (
    start "" "build\reports\jacoco\test\html\index.html"
    echo JaCoCo HTML report opened in browser!
) else (
    echo JaCoCo HTML report not found! Run tests first.
)
goto end

:run_powershell
echo.
echo [RUNNING POWERSHELL SCRIPT...]
powershell -ExecutionPolicy Bypass -File "run_tests.ps1"
goto :end

:show_results
echo.
echo ========================================
echo          TESTING COMPLETED   
echo ========================================
echo.
echo Results:
echo - JaCoCo HTML report: build\reports\jacoco\test\html\index.html
echo - JaCoCo XML report: build\reports\jacoco\test\jacocoTestReport.xml
echo - Test HTML report: build\reports\tests\test\index.html
echo - Code coverage: Check JaCoCo report
echo.
echo 📝 Полный лог сохранен в: %LOG_FILE%
pause
goto :end

:invalid_choice
echo Invalid choice! Please enter 1-5.
pause
goto :end

:end
echo.
echo Goodbye!
echo 📝 Лог сохранен в: %LOG_FILE%
) > "%LOG_FILE%" 2>&1

set EXIT_CODE=%ERRORLEVEL%

REM Показываем вывод на экране
type "%LOG_FILE%"

echo.
echo ========================================
echo 📝 Лог сохранен в: %LOG_FILE%
echo Код завершения: %EXIT_CODE%
echo ========================================
echo.
echo Хотите открыть файл лога? (Y/N)
set /p open="> "
if /i "%open%"=="Y" (
    notepad "%LOG_FILE%"
)

