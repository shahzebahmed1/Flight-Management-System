# Flight Management System

A Java desktop application for managing flight reservations, bookings, and payments. Built with Swing GUI and MySQL database.

## Features

- **Customer Features** (No login required)
  - Search flights by origin, destination, date, or airline
  - Book flights with seat selection
  - View and manage bookings
  - Payment processing integrated with bookings

- **Admin Features** (Login required)
  - Add, update, and delete flights
  - View all flights in the system
  - Manage flight schedules

- **Employee Features** (Login required)
  - View all bookings
  - Modify customer reservations
  - Manage customer data
  - View flight schedules

## Requirements

- Java JDK 8 or higher
- MySQL Database (5.7 or higher)
- MySQL JDBC Driver (included in `mysql-connector-j-9.5.0` folder)

## Setup

### 1. Database Setup

1. Open MySQL Workbench
2. Create the database:
   ```sql
   CREATE DATABASE flightdb;
   ```
3. Select the database (double-click `flightdb` in Schemas)
4. Open and run `database.sql` file:
   - File → Open SQL Script
   - Select `database.sql`
   - Click Execute (lightning bolt icon)

### 2. Configure Database Connection

Update the database password in these files:
- `flight/database/DatabaseConnectivity.java` (line 12)
- `payment/database/DatabaseConnectivity.java` (line 12)

Change the password to match your MySQL root password.

## How to Compile and Run

1. **Compile all Java files:**
   ```bash
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar" booking/*.java
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar" flight/*.java flight/database/*.java
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar" payment/*.java payment/database/*.java
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;flight;payment" auth/UserDAO.java
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;flight;payment" booking/BookingDAO.java
   javac -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;flight;payment;auth;booking" CustomerGUI.java
   ```

2. **Run the application:**
   ```bash
   java -cp ".;mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;flight;payment;auth;booking" CustomerGUI
   ```

## Default Login Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`

**Employee:**
- Username: `employee`
- Password: `emp123`

## Project Structure

```
Flight-Management-System/
├── booking/              # Booking management
├── flight/               # Flight management
├── payment/              # Payment processing
├── auth/                 # Authentication
├── database.sql          # Database schema
└── CustomerGUI.java      # Main application
```

## Troubleshooting

**"MySQL JDBC Driver not found"**
- Make sure `mysql-connector-j-9.5.0` folder exists in project root
- Check that `mysql-connector-j-9.5.0.jar` is inside that folder

**"Access denied for user"**
- Verify MySQL username and password in DatabaseConnectivity.java files
- Make sure MySQL server is running

**"Unknown database 'flightdb'"**
- Run the `database.sql` script in MySQL Workbench
- Make sure you've selected the `flightdb` database

**Compilation errors**
- Ensure Java JDK 8+ is installed
- Check that all files are in correct package directories
