# Goalie — Tournament Management System

A full-stack web application for managing sports tournaments, built as a 
team Software Engineering project at Douglas College.

![Java](https://img.shields.io/badge/java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/spring_boot-3.x-6DB33F?style=flat-square&logo=springboot)
![HTML](https://img.shields.io/badge/frontend-HTML%2FCSS%2FJS-blue?style=flat-square)

## What It Does

Goalie allows tournament organizers to create and manage sports tournaments 
end-to-end — registering teams, scheduling matches, recording results, and 
tracking standings — through a multi-role web interface.

## Features

- **Team registration** — Add and manage participating teams
- **Match scheduling** — Generate and assign fixtures across rounds
- **Result tracking** — Record scores and update standings automatically
- **Multi-role access** — Separate flows for admins and participants
- **User authentication** — Secure login with role-based access control
- **Data persistence** — All tournament data stored in a relational database

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Maven |
| Architecture | MVC, REST endpoints |
| Database | H2 (relational, SQL) |
| Frontend | HTML, CSS, JavaScript, Thymeleaf |
| Version Control | Git, GitHub (feature branch workflow) |
| Methodology | Agile/Scrum — 4-month SDLC |

## Project Structure
src/
├── main/
│   ├── java/
│   │   └── com/goalie/
│   │       ├── controllers/    # MVC route handlers
│   │       ├── models/         # Entity classes
│   │       ├── repositories/   # Data access layer
│   │       └── services/       # Business logic
│   └── resources/
│       ├── templates/          # Thymeleaf HTML views
│       └── application.properties

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Locally

```bash
# Clone the repo
git clone https://github.com/Jud-e/Goalie.git
cd Goalie

# Build and run
./mvnw spring-boot:run
```

Open http://localhost:8080

## Team

Built by a team of 4 as part of the Software Engineering course at 
Douglas College (Sep – Dec 2025). Delivered on time with a top grade.

## About

A hands-on team project covering the full software development lifecycle — 
requirements gathering, system design, implementation, code review, testing, 
and WCAG 2.1 AA accessibility compliance.
