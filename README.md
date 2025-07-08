# NexusMart API

This is the backend REST API for the NexusMart E-commerce Platform, built with Spring Boot.

## Prerequisites
- Java 21
- Maven 3+
- PostgreSQL

## How to Run

1.  **Clone the repository:**
    `git clone [your-github-repo-url]`

2.  **Setup the database:**
    - Make sure PostgreSQL is running.
    - Create a new database named `nexusmart_db`.

3.  **Configure the application:**
    - Open the `src/main/resources/application.properties` file.
    - Update the `spring.datasource.username` and `spring.datasource.password` with your PostgreSQL credentials.

4.  **Run the application:**
    - You can run the `NexusmartApiApplication.java` main class directly from your IDE.
    - Or, from the project's root directory, run the Maven command: `mvn spring-boot:run`

5.  **Access the API Documentation:**
    - Once the application is running, open your browser and go to:
      `http://localhost:8080/swagger-ui.html`