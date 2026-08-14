# 🏥 SmartCare — Integrated Healthcare Management & Telemedicine System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**SmartCare** is a full-stack healthcare platform that connects patients, doctors, and administrators in one place — from booking appointments and video consultations to medicine reminders, pharmacy, ambulance booking, and an AI-powered symptom checker.

> Course project for **CSE470 (Software Engineering)** at **BRAC University**, built with an Agile, sprint-based workflow across four sprints.

## ✨ Features

**Accounts & Access**
- 🔐 Role-Based Access Control (RBAC)
- 👤 User Profile & Health Profile management
- 🧭 Role-specific dashboards (patient, doctor, admin)

**Care & Consultation**
- 🔎 Search doctors by specialty
- 📅 Online appointment booking with reminders
- 🎥 Telemedicine — video/audio consultation
- 🤖 AI-powered symptom checker (self-hosted Ollama model)

**Services**
- 💊 Pharmacy service, medicine reminder & medicine history
- 🚑 Ambulance booking (ride-sharing model)
- 💳 Online payment
- 🗺️ Map integration

**Community & Content**
- 📝 Health articles & blogs
- ⭐ Ratings & reviews (doctors, hospitals, ambulance, pharmacy)
- 🤝 Donation & crowdfunding
- ❓ FAQ

## 🛠️ Tech Stack

- **Backend:** Java, Spring Boot — layered MVC (controller / service / repository / model)
- **Frontend:** HTML, Bootstrap
- **Database:** MySQL (Spring Data JPA + plain JDBC modules)
- **AI:** Self-hosted Ollama model powering the symptom checker
- **Infrastructure:** Docker Compose (MySQL)

## 🏗️ Project Structure

- **controller/** — request handling and view/REST controllers
- **service/** — business logic
- **repository/** — data access (JPA + JDBC)
- **model/** — entities and DTOs
- **src/main/resources/** — templates, static assets, and SQL init scripts

## 🚀 Getting Started

**Prerequisites:** JDK 17+, Maven, and Docker.

1. Start the MySQL database: **docker compose up -d**
2. Run the application: **./mvnw spring-boot:run**
3. Open **http://localhost:8080**

Local database configuration lives in **src/main/resources/application.properties** (local dev credentials only — no production secrets).

## 👥 Team

| Name | GitHub |
|------|--------|
| Mehedi Hasan | [@MehediHasan19131](https://github.com/MehediHasan19131) |
| Rony Miah | [@Saiful101](https://github.com/Saiful101) |
| Nahian Mahmud | [@nahianmahmud-2k1](https://github.com/nahianmahmud-2k1) |
| Imtiaz Zaman Sami | [@imtiazzamansami-arch](https://github.com/imtiazzamansami-arch) |

## 📌 Status

Actively developed across Sprints 1–4 using an Agile workflow. Browse the branches and commit history for module-by-module progress.
