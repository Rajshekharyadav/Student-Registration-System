# Student Registration Management System

A web-based system for managing student registration in educational institutions, built with Spring Boot.

## Features

- **Automated Registration**: Web-based student registration form with validation
- **Data Management**: Secure storage and retrieval of student records
- **Admin Panel**: Interface for administrators to approve/reject registrations
- **Status Tracking**: Real-time status updates for registration applications
- **Validation**: Input validation to ensure data accuracy

## Technology Stack

- **Backend**: Spring Boot 2.7.0
- **Database**: H2 (in-memory for development)
- **Frontend**: Thymeleaf + Bootstrap 5
- **Security**: Spring Security
- **Build Tool**: Maven

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Running the Application

1. Navigate to the project directory:
   ```bash
   cd Student-Registration-System
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. Access the application:
   - Main page: http://localhost:8082
   - Student Registration: http://localhost:8082/register
   - Admin Panel: http://localhost:8082/admin
   - H2 Console: http://localhost:8082/h2-console

## Usage

### For Students
1. Visit the registration page
2. Fill out the registration form with required information
3. Submit the form to register

### For Administrators
1. Access the admin panel
2. View all student registrations
3. Approve or reject pending applications

## Project Structure

```
src/
├── main/
│   ├── java/com/studentreg/
│   │   ├── StudentRegistrationApplication.java
│   │   ├── controller/
│   │   │   └── StudentController.java
│   │   ├── model/
│   │   │   └── Student.java
│   │   ├── repository/
│   │   │   └── StudentRepository.java
│   │   └── service/
│   │       └── StudentService.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── index.html
│           ├── register.html
│           └── admin.html
└── test/
```

## Database Schema

The system uses a single `students` table with the following fields:
- id (Primary Key)
- first_name
- last_name
- email (Unique)
- phone
- date_of_birth
- course
- status (PENDING/APPROVED/REJECTED)