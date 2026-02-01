@echo off
REM Windows batch file to run Balatro.jar from Release folder

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher and try again.
    pause
    exit /b 1
)

REM Check if JAR file exists in current directory
if not exist "Balatro.jar" (
    echo Error: Balatro.jar not found in current directory
    echo Please ensure Balatro.jar is in the Release folder.
    pause
    exit /b 1
)

REM Run the JAR file with all passed arguments
java -jar Balatro.jar %*
