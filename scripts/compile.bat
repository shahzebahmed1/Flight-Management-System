@echo off
REM Compile script for Flight Management System
REM This script compiles all Java source files in the project

echo Building Flight Management System...
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

REM Compile booking package
echo [1/6] Compiling booking package...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight" -encoding UTF-8 booking\*.java
if errorlevel 1 goto :error

REM Compile flight package
echo [2/6] Compiling flight package...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar" -encoding UTF-8 flight\*.java flight\database\*.java
if errorlevel 1 goto :error

REM Compile payment package
echo [3/6] Compiling payment package...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight" -encoding UTF-8 payment\*.java
if errorlevel 1 goto :error

REM Compile auth package
echo [4/6] Compiling authentication package...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight;payment" -encoding UTF-8 auth\UserDAO.java
if errorlevel 1 goto :error

REM Compile booking DAO
echo [5/6] Compiling booking DAO...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight;payment" -encoding UTF-8 booking\BookingDAO.java
if errorlevel 1 goto :error

REM Compile main GUI
echo [6/6] Compiling main application...
javac -cp ".;mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;flight;payment;auth;booking" -encoding UTF-8 CustomerGUI.java
if errorlevel 1 goto :error

echo.
echo [SUCCESS] All files compiled successfully!
echo You can now run the application using scripts\run.bat
echo.
pause
exit /b 0

:error
echo.
echo [ERROR] Compilation failed. Please check the errors above.
echo.
pause
exit /b 1

