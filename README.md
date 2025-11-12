# PojisteniApp

**PojisteniApp** is a simple web-based insurance management system built with Java. It allows users to manage client records and insurance policies through a basic user interface. This application was created as a learning project to practice working with Java, Spring Boot, servlets, and a simple web frontend.
##  Features

- Add, edit, and delete client records
- Add, edit, and delete insurance policies
- View all registered clients and their insurance details
- Web interface built with HTML, CSS, and JavaScript
- Java backend using servlets
- MySQL database (via XAMPP)

##  Getting Started

### Prerequisites

- Java 17+
- IntelliJ IDEA (or other Java IDE)
- XAMPP (MySQL server)

### Running the App

1. Clone this repository:
   ```bash
   git clone https://github.com/KajcaK/PojisteniApp.git
   cd PojisteniApp
   ```

2. Start your MySQL server using XAMPP.

3. Open the project in IntelliJ IDEA.

4. Configure your database connection in the code:
   - Make sure the JDBC connection URL, username, and password match your local MySQL setup.
   - Default setup assumes:
     - URL: `jdbc:mysql://localhost:3306/pojisteni`
     - Username: `root`
     - Password: *(leave empty or adjust as needed)*

5. Run the project using your IDE's built-in servlet support.

6. Open the app in your browser:
   ```
   http://localhost:8080/
   ```

##  Project Structure

```
PojisteniApp/
├── src/
│   ├── main/
│   │   ├── java/                # Java source files
│   │   └── resources/           
│   │       ├── static/          # Static files
│   │       └── templates/       # HTML templates

```

## 🛠 Technologies Used

- Java (Spring Boot)
- Spring MVC (Controllers, Services, Repositories)
- Thymeleaf (HTML template engine)
- ApexCharts (JavaScript chart library for statistics)
- HTML, CSS, JavaScript
- Maven (build tool)
- MySQL (via XAMPP)
- IntelliJ IDEA (IDE)
