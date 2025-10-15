@echo off
chcp 65001 >nul
echo Creating animations directory...
if not exist "src\main\resources\animations" mkdir "src\main\resources\animations"
echo Copying GIF files...
xcopy "Animation\*.gif" "src\main\resources\animations\" /Y /I
echo Done!
pause

