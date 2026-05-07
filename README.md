FSCValet

FSCValet is a desktop valet parking application developed for Farmingdale State College as part of a Software Engineering group project.
The system was created to address one of the biggest challenges on campus: parking. 
FSCValet gives students and staff a more convenient way to manage vehicle drop-off, parking updates, and retrieval requests 
through a structured JavaFX desktop interface.

Project Overview

Parking at Farmingdale State College can be stressful, especially during busy class hours. FSCValet was designed as a solution 
that helps reduce the time spent searching for parking by introducing a valet-based system. 
The application allows customers to check in vehicles, view ticket and vehicle status information, and request vehicle retrieval, 
while staff members manage parking activity from a dashboard.

This repository contains the frontend and backend structure of the FSCValet system in a Maven-based Java project. 
The application uses JavaFX for the client interface and Spring Boot for backend support, with Firebase included for data management 
services. 

Team Roles

- Jahnelle Bigby – Project Manager / Frontend Development  
  Led project planning, task assignment, sprint coordination, scrum communication, GitHub organization,
  and contributed to frontend design and dashboard-related UI work.

- Adriana Lambert – Frontend Development  
  Contributed to screen design, JavaFX interface layout, and CSS styling.

- Jayden Montalvo – Backend Development  
  Contributed to backend logic, project structure, and core application functionality.

- Cobin Black – Backend Development  
  Contributed to navigation, backend integration, and system support.

Project Objectives

- Reduce parking difficulty for FSC students and staff
- Provide a structured digital valet parking workflow
- Allow customers to view vehicle and ticket status
- Support valet staff with a dashboard for monitoring parking activity
- Demonstrate software engineering practices through collaborative development

Features

Customer Features
- Secure login
- Vehicle check-in support
- Ticket-based vehicle tracking
- Vehicle retrieval request flow
- Vehicle status viewing

Staff Features
- Staff dashboard
- Active parking overview
- Parking activity monitoring
- Vehicle and ticket status updates
- Parking lot management support

Design Process

The FSCValet interface was first planned in Figma using LoFi and HiFi prototypes (by Jayden and Adrianna) before being implemented 
as a JavaFX desktop application. This process helped the team organize the app layout, improve screen flow, and keep the interface 
consistent throughout development.

Tech Stack

- **Language:** Java
- **Frontend:** JavaFX, CSS
- **Backend:** Spring Boot
- **Database / Services:** Firebase Admin SDK
- **Build Tool:** Maven
- **IDE:** IntelliJ IDEA
- **Version Control:** GitHub
- **Project Management:** SCRUM, GitHub Projects 

The project structure and dependencies are managed in `pom.xml`, which includes Spring Boot, JavaFX controls/FXML, Firebase Admin SDK, 
and Maven plugins for both the server and JavaFX client.
