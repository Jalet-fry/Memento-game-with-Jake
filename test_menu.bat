@echo off
chcp 65001 >nul

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
echo 1. Run all tests with JaCoCo coverage
echo 2. Run only successful tests
echo 3. Open JaCoCo HTML report (if exists)
echo 4. Create detailed report (PowerShell)
echo 5. Exit
echo.
set /p choice="Enter number (1-5): "

if "%choice%"=="1" goto run_all_tests
if "%choice%"=="2" goto run_successful_tests
if "%choice%"=="3" goto open_jacoco_report
if "%choice%"=="4" goto run_powershell
if "%choice%"=="5" goto end
goto invalid_choice

:run_all_tests
echo.
echo [RUNNING ALL TESTS WITH JACOCO...]
call .\gradlew.bat clean test --rerun-tasks jacocoTestReport openJacocoReport
set TEST_EXIT_CODE=%ERRORLEVEL%
if "%TEST_EXIT_CODE%"=="0" (
    echo.
    echo [TESTS COMPLETED SUCCESSFULLY]
    echo JaCoCo HTML report should have opened automatically!
) else (
    echo.
    echo [TESTS FAILED WITH EXIT CODE: %TEST_EXIT_CODE%]
    echo Please check the output above for details.
)
goto show_results

:run_successful_tests
echo.
echo [RUNNING SUCCESSFUL TESTS ONLY...]
call .\gradlew.bat test --continue
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
pause
goto :end

:invalid_choice
echo Invalid choice! Please enter 1-5.
pause
goto :end

:end
echo.
echo Goodbye!