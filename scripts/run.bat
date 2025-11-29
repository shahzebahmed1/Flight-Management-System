@echo off
REM Run script for Flight Management System
REM This script launches the main application

echo Launching Flight Management System...
echo.

REM Change to project root directory
cd /d "%~dp0.."

REM Verify MySQL connector exists
if not exist "mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar" (
    echo [ERROR] MySQL JDBC driver not found!
    echo Please ensure mysql-connector-j-9.5.0.jar is in the mysql-connector-j-9.5.0 folder
    pause
    exit /b 1
)

REM Verify CustomerGUI class exists
if not exist "CustomerGUI.class" (
    echo [WARNING] CustomerGUI.class not found. Compiling first...
    call "%~dp0compile.bat"
    if errorlevel 1 (
        echo [ERROR] Compilation failed. Please fix errors and try again.
        pause
        exit /b 1
    )
)

REM Run the application
java -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight;payment;auth;booking" CustomerGUI

if errorlevel 1 (
    echo.
    echo [ERROR] Application failed to start. Check the error messages above.
    pause
    exit /b 1
)

