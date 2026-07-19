# Smart Bus Hygiene Monitoring – Setup Guide

## Prerequisites
- Java 17
- Maven
- MySQL 8.x
- IntelliJ IDEA

## Step 1: Database Setup
Open MySQL Workbench and run the entire `database/setup.sql` file.
This creates:
- `bus_feedback_db` database
- `admin` table (with default login: admin@busfeedback.in / admin123)
- `bus` table (5 sample buses)
- `feedback` table (with new hygiene fields)
- `alerts` table
- `hygiene_status` table
- Sample data

## Step 2: Configure Password
Open `src/main/resources/application.properties` and change:
```
spring.datasource.password=your_password
```
to your actual MySQL root password.

## Step 3: Open in IntelliJ
File → Open → Select the `BusFeedbackSB_UPGRADED` folder.
Wait for Maven to download dependencies.

## Step 4: Run
Right-click `BusFeedbackApplication.java` → Run.
App starts on: http://localhost:8080

## Page URLs
| Page              | URL                         |
|-------------------|-----------------------------|
| Home              | http://localhost:8080/      |
| Feedback Form     | http://localhost:8080/feedback.html?busId=BUS001 |
| About             | http://localhost:8080/about.html |
| Contact           | http://localhost:8080/contact.html |
| Admin Login       | http://localhost:8080/admin/login |
| Admin Dashboard   | http://localhost:8080/admin/dashboard |
| QR Codes          | http://localhost:8080/qr-codes.html |

## Default Admin Login
- Email: admin@busfeedback.in
- Password: admin123

## Alert Logic (No AI – Pure Java Conditions)
| Condition               | Action                     |
|-------------------------|----------------------------|
| Rating <= 2             | Mark bus Unclean + Alert   |
| Vomit = Yes             | IMMEDIATE Alert + Unclean  |
| Bad Smell = Yes         | Complaint alert            |
| Muddy Floor = Yes       | Complaint alert            |
| Dirty Seats = Yes       | Complaint alert            |
| 2+ complaints on 1 bus  | Mark bus Unclean + Alert   |

## Project Structure
```
src/main/java/com/busfeedback/
  BusFeedbackApplication.java       ← Main class
  controller/
    AdminController.java            ← Login, Dashboard, actions
    FeedbackController.java         ← REST API for feedback form
    QRController.java               ← QR code generator
  model/
    Admin.java                      ← Admin entity
    Feedback.java                   ← Feedback entity
    Alert.java                      ← Alert entity
    HygieneStatus.java              ← Bus hygiene tracking
  repository/
    AdminRepository.java
    FeedbackRepository.java
    AlertRepository.java
    HygieneStatusRepository.java
  service/
    FeedbackService.java            ← All business logic + alert logic

src/main/resources/
  application.properties
  templates/
    admin-login.html                ← Admin login page (Thymeleaf)
    admin-dashboard.html            ← Admin dashboard (Thymeleaf + Chart.js)
  static/
    index.html
    feedback.html                   ← Feedback form with all hygiene fields
    about.html
    contact.html
    qr-codes.html
    css/nav.css

database/
  setup.sql                         ← Run this first in MySQL
```
