@echo off
REM Build script to create Balatro.jar release file in Release folder
REM This script should be run from the Release directory

echo Building Balatro release...

REM Get the script directory (Release folder)
set SCRIPT_DIR=%~dp0
set SCRIPT_DIR=%SCRIPT_DIR:~0,-1%
set PROJECT_ROOT=%SCRIPT_DIR%\..

REM Change to Release directory
cd /d "%SCRIPT_DIR%"

REM Clean up old build artifacts
echo Cleaning old files...
if exist Balatro.jar del /f Balatro.jar
if exist build-temp rmdir /s /q build-temp

REM Create temporary build directory
echo Creating build directory...
mkdir build-temp

REM Compile all Java source files from project root
echo Compiling Java source files...
cd /d "%PROJECT_ROOT%"
javac -encoding UTF-8 -d "%SCRIPT_DIR%\build-temp" ^
    Source\model\*.java ^
    Source\service\*.java ^
    Source\data\*.java ^
    Source\ui\*.java ^
    Source\main\Main.java
cd /d "%SCRIPT_DIR%"

if %errorlevel% neq 0 (
    echo Error: Compilation failed!
    rmdir /s /q build-temp
    pause
    exit /b 1
)

echo Compilation successful!

REM Create MANIFEST.MF with Main-Class entry
echo Creating manifest...
mkdir build-temp\META-INF
(
echo Manifest-Version: 1.0
echo Main-Class: main.Main
) > build-temp\META-INF\MANIFEST.MF

REM Package into JAR file in current directory (Release folder)
echo Packaging JAR file...
cd build-temp
jar cfm ..\Balatro.jar META-INF\MANIFEST.MF *
cd ..

REM Clean up temporary files
echo Cleaning up...
rmdir /s /q build-temp

echo.
echo Build complete!
echo JAR file created: Balatro.jar
echo.
echo To run the game:
echo   Balatro.bat              (GUI mode)
echo   Balatro.bat --cli         (CLI mode)
echo.
pause

