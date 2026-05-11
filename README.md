# FSCValet


## Table of Contents
- Summary & Project Overview
- Project Objectives
- Team Members & Roles
- Technologies
- Setup
  - Prerequisites
  - Instructions
- Features
- Intended Users
- Figma Design Process
- Interface Screens 
- Project Management
- Status

## Summary & Project Overview

FSCValet is a desktop valet parking application developed for Farmingdale State College as part of a Software Engineering group project. The system was created to address one of the biggest challenges on campus: parking. FSCValet gives students and staff a more convenient way to manage vehicle drop-off, parking updates, and retrieval requests through a structured JavaFX desktop interface.

Parking at Farmingdale State College can be stressful, especially during busy class hours. FSCValet was designed as a solution that helps reduce the time spent searching for parking by introducing a valet-based system. The application allows customers to check in vehicles, view ticket and vehicle status information, and request vehicle retrieval, while staff members manage parking activity from a dashboard.

This repository contains the frontend and backend structure of the FSCValet system in a Maven-based Java project. The application uses JavaFX for the client interface and Spring Boot for backend support, with Firebase included for data management services.

## Project Objective 

-Reduce parking difficulty for FSC students and staff

-Provide a structured digital valet parking workflow

-Allow customers to view vehicle and ticket status

-Support valet staff with a dashboard for monitoring parking activity

-Demonstrate software engineering practices through collaborative development

## Team Members & Roles 

Jahnelle Bigby – Project Manager / Frontend Development
Led project planning, task assignment, sprint coordination, scrum communication, GitHub organization, and contributed to frontend design and dashboard-related UI work.

Adriana Lambert – Frontend Development
Contributed to screen design, JavaFX interface layout, and CSS styling.

Jayden Montalvo – Backend Development
Contributed to backend logic, project structure, and core application functionality.

Cobin Black – Backend Development
Contributed to navigation, backend integration, and system support.

## Technologies

- IntelliJ IDEA – Primary IDE
- Java – Main programming language
- JavaFX – Desktop user interface
- CSS – Interface styling
- Spring Boot – Backend support
- Firebase Admin SDK – Data management services
- Maven – Dependency management and build tool
- GitHub – Version control and collaboration
- Figma – LoFi and HiFi interface prototyping

## Setup

### Prerequisites
1. Java installed
2. Maven installed
3. IntelliJ IDEA recommended

### Instructions
1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Allow Maven to install dependencies from `pom.xml`
4. Run the JavaFX client from `client.ClientMain`
5. Run the backend server from `server.ServerApplication`

## Features

Customer Features:

-Secure login

-Vehicle check-in support

-Ticket-based vehicle tracking

-Vehicle retrieval request flow

-Vehicle status viewing


Staff Features:

-Staff dashboard

-Active parking overview

-Parking activity monitoring

-Vehicle and ticket status updates

-Parking lot management support

## Intended Users

- FSC Students
- FSC Staff
- Valet Attendants
- System Administrators

## Figma Design Process
## LoFi Images
![Lofi](images/lofi.png)
![Lofi](images/lofi2.png) 
## HiFi Images
![Hifi](images/checkin.png)
![Hifi](images/myticket.png)
![Hifi](images/requestpickup.png)
![Hifi](images/dashboard.png)
![Hifi](images/checkinstaff.png)
![Hifi](images/activevehicles.png)

## Interface Screens of our Project  
## Login Page 
![Hifi](images/loginlogin.png)
## Create Account 

## Profile Page 
![Hifi](images/profile1client.png)
![Hifi](images/profile2client.png)
## Client Dashboard

## Vehicle Check In 
![Hifi](images/vehiclecheckinclient.png)
## Car Details 
![Hifi](images/cardetailsclient.png)
## Confirmation Page 
![Hifi](images/confirmclient.png)
## Edit Request 
![Hifi](images/editrequestclient.png)
## Request History 
![Hifi](images/requesthistoryclient.png)
## Staff Dashboard
![Hifi](images/staffportal.png)


## Project Management

The project was managed using a SCRUM-style workflow. Work was divided into sprint goals and team responsibilities. GitHub Projects was used to organize issues, assign tasks, manage views, and track progress. GitHub also helped maintain commit history, collaboration, and code organization through branches and updates.

## Status

FSCValet is a working academic prototype completed as part of a Software Engineering final project. The project demonstrates the planning, interface design, development, and collaboration process involved in building a valet parking system for Farmingdale State College.

Future improvements may include expanded real-time features, improved notifications, stronger authentication flow, and enhanced geofencing support.
