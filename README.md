# Smart Task Management System
The Smart Task Management System is a web-based application designed to help users manage tasks effectively. It allows users to create, update, and track tasks while managing priorities, deadlines, and receiving notifications. This system provides essential features such as secure authentication, task management, and email notifications for task updates.


## Prerequisites

Before running the application, ensure that you have the following installed on your machine:

- **Java 11 or above** (for running the backend)
- **Node.js and npm** (for running the frontend)
- **MySQL** (for database management)
- **Maven** (for building and running the backend)
- **Git** (for cloning the repository)

## Setup Instructions

Follow the steps below to set up the Smart Task Management System on your local machine.

### Backend Setup

#### 1. Clone the Repository

Clone the repository to your local machine using the following command:

```
git clone https://github.com/alokpandeygzp/TaskAlert-Smart-Task-Management-System
cd TaskAlert-Smart-Task-Management-System
```

#### 2. Configure Database

Install and set up MySQL.
Create a new database, e.g., `task_management`.
Configure the `application.properties` file in `src/main/resources/` for MySQL connection:

```
spring.datasource.url=jdbc:mysql://localhost:3306/task_management
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

#### 3. Build the Backend

Use Maven to clean and install the necessary dependencies for the backend:

`
mvn clean install
`

#### 4. Run the Backend

To start the backend, run the following command:

`
mvn spring-boot:run
`

The backend will now be running at http://localhost:8080 by default.

### Frontend Setup

#### 1. Navigate to the Frontend Directory

Change into the frontend directory:


`
cd frontend
`
#### 2. Install Dependencies

Install the necessary frontend dependencies with npm:


`
npm install
`

#### 3. Run the Frontend

To start the frontend development server, run:


`
npm start
`

The frontend will now be running at http://localhost:3000.


## Key Endpoints (Backend)

The backend exposes the following key API endpoints:

### User Authentication
- POST /api/auth/register: Register a new user.
- POST /api/auth/login: Login with credentials and receive a JWT token.

### Task Management
- GET /api/tasks/: Get all tasks for the authenticated user.
- POST /api/tasks/: Create a new task.
- PUT /api/tasks/{taskId}: Update an existing task.
- DELETE /api/tasks/{taskId}: Delete a task.

### Notifications
- The backend sends email notifications for tasks with upcoming deadlines.

## Dependencies

### Backend (Spring Boot)
Here are the key dependencies for the backend:

- Spring Boot Starter Web: To create REST APIs.
- Spring Boot Starter Security: For user authentication and role management.
- Spring Boot Starter Data JPA: For database interaction using JPA.
- MySQL Connector: For connecting to MySQL database.
- JWT: For secure authentication with JSON Web Tokens.
- JavaMailSender: For sending email notifications.


## Conclusion

The Smart Task Management System provides a clean, efficient platform for managing tasks. By following the steps above, you can run the system on your local machine. It offers key features like task prioritization, real-time notifications, and secure user authentication, aimed at improving productivity and task organization.

