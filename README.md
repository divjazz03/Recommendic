

<h2 align="center">A Medical Consultation and Recommendation Application </h2>

## 📑 Table of Contents

1. 🤖 [Introduction](#-introduction)
2. 🔋 [Features](#-features)
3. 🔩 [Tech Stack](#-tech-stack) 
4. 💃 [Quick Start](#-quick-start)


## 🤖 Introduction
The idea of this application is inspired by the idea and observation of the hassles encountered in medical consultations due to distance and or location.
This application brings the patients to certified medical practitioners of various disciplines from anywhere from around the world or even from their own communities for physical consultation and check-up scheduling.
This solves the problem of finding certified medical practitioners especially in an area where the admin is unfamiliar with.
This app emphasises on ease and great admin experiences for all our users.</p>

## 🔋 Features
- 👉 **User**: This encapsulates all the users of the platform of which include the `patients`, `admins`,`super_admins` and certified `consultants`.
- 👉 **Registration**: This is the conversion of a guest to an authenticated user. The guest can be converted to a `PATIENT` or a `CONSULTANT` at a time. To qualify as a verified `CONSULTANT`, the `ADMIN` assigned must verify the authenticity of the certification, or in later versions can be verified by artificial intelligence.
- 👉 **Login**: This logs in the users into the platform. It uses JWT Authentication to verify user identity. It makes the feeds and data displayed to them personalized according to their recent activities or authorization level.
- 👉 **Recommendation**: This is unique to the `patients`. It recommends consultants or articles according to search patterns, location or recent activities.
- 👉 **Real Time Consultation and Chat**: This involves real time consultation/chats with consultants whom the admin has started a consultation with. It utilizes web sockets. The admin can have a real-time conversation with the practitioner of their choosing about topics they are interested in.
- 👉 **Admin Registration**: An `ADMIN` can be registered as an admin by a `SUPER_ADMIN` and an automatically generated password is assigned to that admin.
- 👉 **Consultant Certification**: Admins are assigned the certificates of the consultants that haven't been certified. Once the assigned admin certifies the certificates. The Consultant is automatically verified.
- 👉 **File upload**: The Consultant can upload pdf documents or clear images of their certificates. Everyone consultant and patients need to upload their profile pictures for visual identification.
- 👉 **Email Notifications**: Implements email notifications for account confirmation and reset of passwords

## 🔩 Tech Stack
- SpringBoot
- Hibernate
- Java
- Docker
- Postgresql

## 💃 Quick Start
Follow these steps to set up the project locally on your machine:

**Prerequisites**
Make sure you have the following installed on your machine:

- [Docker](https://www.docker.com)
- [PostMan](https://www.postman.com)

**Cloning the Repository**
```bash
git clone https://github.com/divjazz03/recommendic
```

