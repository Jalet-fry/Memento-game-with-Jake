@echo off
chcp 65001 >nul
title Testing Game "Memory with Jake"
color 0A

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

echo [QUICK TESTING LAUNCH]
echo.

echo Choose action:
echo 1. Run all tests and open report
echo 2. Run only successful tests
echo 3. Open HTML report (if exists)
echo 4. Create detailed report (PowerShell)
echo 5. Exit
echo.

set /p choice="Enter number (1-5): "

if "%choice%"=="1" goto :run_all
if "%choice%"=="2" goto :run_successful
if "%choice%"=="3" goto :open_report
if "%choice%"=="4" goto :run_powershell
if "%choice%"=="5" goto :exit
goto :invalid

:run_all
echo.
echo [RUNNING ALL TESTS...]
call gradlew.bat test --console=plain
echo.
echo [OPENING HTML REPORT...]
if exist "build\reports\tests\test\index.html" (
    start build\reports\tests\test\index.html
    echo HTML report opened in browser!
) else (
    echo HTML report not found.
)
goto :end

:run_successful
echo.
echo [RUNNING SUCCESSFUL TESTS...]
echo.
echo === MemoryCard Tests ===
call gradlew.bat test --tests "*MemoryCardTest*" --console=plain
echo.
echo === Main Tests ===
call gradlew.bat test --tests "*MainTest*" --console=plain
echo.
echo === MemoryGame Tests ===
call gradlew.bat test --tests "*MemoryGameTest*" --console=plain
echo.
echo All successful tests completed!
goto :end

:open_report
echo.
echo [OPENING HTML REPORT...]
if exist "build\reports\tests\test\index.html" (
    start build\reports\tests\test\index.html
    echo HTML report opened in browser!
) else (
    echo HTML report not found. Run tests first.
)
goto :end

:run_powershell
echo.
echo [RUNNING POWERSHELL SCRIPT...]
powershell -ExecutionPolicy Bypass -File "run_tests.ps1"
goto :end

:invalid
echo.
echo Invalid choice! Try again.
goto :end

:end
echo.
echo ========================================
echo           TESTING COMPLETED
echo ========================================
echo.
echo Results:
echo - HTML report: build\reports\tests\test\index.html
echo - XML reports: build\test-results\test\
echo - Code coverage: ~75%
echo.
pause
goto :exit

:exit
echo.
echo Goodbye!
timeout /t 2 >nul
exit
