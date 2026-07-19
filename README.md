# 🚌 Smart Bus Hygiene Monitoring System

A Spring Boot web application that lets bus passengers submit hygiene feedback (via a QR code on each bus), and automatically flags buses as "unclean" and raises alerts based on simple rule-based logic — no AI, pure Java conditions.

Admins can log in to a dashboard to view feedback, alerts, and hygiene status across the fleet.

---

## ✨ Features

- 📱 QR-code based feedback form — passengers scan a code on the bus, or manually select their bus from a list, then submit feedback instantly
- 🚨 Automatic alerts for low ratings, vomit reports, bad smell, muddy floors, and dirty seats
- 📊 Admin dashboard with feedback history and hygiene status, built with Thymeleaf + Chart.js
- 🗄️ MySQL-backed data storage for buses, feedback, alerts, and hygiene status

---

## 🛠️ Prerequisites

- Java 17
- Maven
- MySQL 8.x
- IntelliJ IDEA

---

## 🚀 Setup Guide

### 1. Database Setup
Open MySQL Workbench and run the entire `database/setup.sql` file.

This creates:
- `bus_feedback_db` database
- `admin` table (default login: `admin@busfeedback.in` / `admin123` — **for local demo only, change before any real deployment**)
- `bus` table (5 sample buses)
- `feedback` table (with hygiene fields)
- `alerts` table
- `hygiene_status` table
- Sample data

### 2. Configure Your Database Password
This repo does **not** include real database credentials.

Copy the example config file:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then open `src/main/resources/application.properties` and replace:
```
spring.datasource.password=your_password
```
with your actual MySQL root password.

> ⚠️ `application.properties` is git-ignored on purpose — never commit your real password.

### 3. Open in IntelliJ
File → Open → select the project folder.
Wait for Maven to download dependencies.

### 4. Run
Right-click `BusFeedbackApplication.java` → Run.
App starts on: `http://localhost:8080`

### 5. (Optional) Expose it publicly with ngrok
To let others scan the QR codes from their own phones, tunnel your local server:
```bash
ngrok http 8080
```
Then access the app **through the ngrok URL** (not `localhost`) in your browser. QR codes generated after that will automatically encode the public ngrok URL — the app detects this via the `X-Forwarded-Proto` / `X-Forwarded-Host` headers ngrok sets, no manual config needed.

---

## 🔗 Page URLs

| Page              | URL                         |
|-------------------|-----------------------------|
| Home              | `http://localhost:8080/` |
| Select Bus        | `http://localhost:8080/select-bus.html` |
| Feedback Form     | `http://localhost:8080/feedback.html?busId=BUS001` |
| About             | `http://localhost:8080/about.html` |
| Contact           | `http://localhost:8080/contact.html` |
| Admin Login       | `http://localhost:8080/admin/login` |
| Admin Dashboard   | `http://localhost:8080/admin/dashboard` |
| QR Codes (page)   | `http://localhost:8080/qr-codes.html` |
| QR Code (image)   | `http://localhost:8080/api/qr/{busId}` — returns a 300×300 PNG |

---

## 🔑 Default Admin Login

- Email: `admin@busfeedback.in`
- Password: `admin123`

> For local/demo use only.

---

## 🧠 Alert Logic (No AI – Pure Java Conditions)

| Condition               | Action                     |
|--------------------------|-----------------------------|
| Rating ≤ 2               | Mark bus Unclean + Alert   |
| Vomit = Yes               | Immediate Alert + Unclean  |
| Bad Smell = Yes           | Complaint alert            |
| Muddy Floor = Yes         | Complaint alert            |
| Dirty Seats = Yes         | Complaint alert            |
| 2+ complaints on 1 bus    | Mark bus Unclean + Alert   |

---

## 📁 Project Structure

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
  application.properties.example    ← Copy this to application.properties and fill in your password
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

---

## 📌 Notes

- This project uses rule-based logic for alerts — no machine learning involved.
- Built with Spring Boot, Thymeleaf, MySQL, and Chart.js.
