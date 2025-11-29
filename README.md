# Flight Management System

A comprehensive Java desktop application for managing flight reservations, bookings, and payments. Built with Java Swing for the user interface and MySQL for data persistence.

## Features

- **Customer Portal**: Search flights, make bookings, select seats, and manage reservations
- **Admin Dashboard**: Manage flights (add, update, delete), view all bookings
- **Employee Dashboard**: Manage customer data, modify reservations, view schedules
- **Payment Processing**: Integrated payment system with credit/debit card support
- **Seat Selection**: Visual seat map with real-time availability

## Requirements

- **Java JDK 8 or higher**
- **MySQL 5.7 or higher**
- **MySQL JDBC Driver** (included in project)

## Quick Start

### 1. Database Setup

1. Open MySQL Workbench and connect to your MySQL server
2. Create the database:
   ```sql
   CREATE DATABASE flightdb;
   ```
3. Select the database (double-click `flightdb` in Schemas)
4. Open and run the `database.sql` file to create tables and sample data

### 2. Configure Database Connection

Update the database password in these files:
- `flight/database/DatabaseConnectivity.java` (line 12)
- `payment/database/DatabaseConnectivity.java` (line 12)

Change the password to match your MySQL root password.

### 3. Compile the Project

Open a terminal in the project root directory and run:

```bash
scripts\compile.bat
```

This will compile all Java source files. Make sure you see "Compilation successful" at the end.

### 4. Run the Application

```bash
scripts\run.bat
```

The application will launch with the customer home screen.

## Using the Application

### Customer Features (No Login Required)

- **Search Flights**: Search by origin, destination, date, or airline (all fields optional)
- **Book Flights**: Select a flight, choose a seat, enter passenger info, and complete payment
- **View Bookings**: Enter your email to see all your reservations
- **Flight Schedule**: Browse all available flights

### Admin Login

- Username: `admin`
- Password: `admin123`
- Features: Add/update/delete flights, view all bookings

### Employee Login

- Username: `employee`
- Password: `emp123`
- Features: Manage customer data, modify reservations, view schedules

## Project Structure

```
Flight-Management-System/
├── scripts/              # Build and run scripts
│   ├── compile.bat       # Compilation script
│   └── run.bat          # Application launcher
├── booking/             # Booking management classes
├── flight/              # Flight management classes
│   └── database/        # Database connectivity
├── payment/             # Payment processing classes
│   └── database/        # Database connectivity
├── auth/                # Authentication classes
├── CustomerGUI.java     # Main application entry point
├── database.sql         # Database schema and initial data
└── mysql-connector-j-9.5.0/  # MySQL JDBC driver
```

## Troubleshooting

**"MySQL JDBC Driver not found"**
- Ensure `mysql-connector-j-9.5.0.jar` exists in the `mysql-connector-j-9.5.0` folder

**"Access denied for user"**
- Check your MySQL password in the DatabaseConnectivity.java files
- Verify MySQL server is running

**"Unknown database 'flightdb'"**
- Make sure you've created the database and run `database.sql`

**Compilation errors**
- Ensure Java JDK 8+ is installed: `java -version`
- Check that all source files are in the correct directories

## Development Notes

- Main entry point: `CustomerGUI.java`
- Database connection settings are in `flight/database/DatabaseConnectivity.java` and `payment/database/DatabaseConnectivity.java`
- Default admin credentials can be changed in the database `Users` table
