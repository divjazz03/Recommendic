<div align="center">
    <div>
        <img src="https://img.shields.io/badge/-Spring-black?style=for-the-badge&logoColor=white&logo=spring&color=6FB33F" alt="spring" />
        <img src="https://img.shields.io/badge/-SPRING_SECURITY-black?style=for-the-badge&logoColor=white&logo=springsecurity&color=6DB33F" alt="spring security" />
        <img src="https://img.shields.io/badge/-JAVA-black?style=for-the-badge&logoColor=white&logo=openjdk&color=000000" alt="openjdk" />
        <img src="https://img.shields.io/badge/-POSTMAN-black?style=for-the-badge&logoColor=white&logo=postman&color=FF4154" alt="postman" />
        <img src="https://img.shields.io/badge/-PostgreSQL-black?style=for-the-badge&logoColor=white&logo=postgresql&color=4169E1" alt="postgresql" />
        <img src="https://img.shields.io/badge/-Docker-black?style=for-the-badge&logoColor=white&logo=docker&color=2496ED" alt="docker" />
        <img src="https://img.shields.io/badge/-Hibernate-black?style=for-the-badge&logoColor=white&logo=hibernate&color=59666C" alt="hibernate" />
    </div>
    <br />
    <h2 align="center">A Medical Consultation and Recommendation Application </h2>
    <div align="center">
        Build this project step by step with this detailed guild
    </div>
</div>
    

## 📑 Table of Contents

1. 🤖 [Introduction](#-introduction)
2. 🔋 [Features](#-features)
3. 🔩 [Tech Stack](#-tech-stack) 
4. 💃 [Quick Start](#-quick-start)
5. 🕸 [Snippets](#-snippets)


## 🤖 Introduction
<p>
This application was inspired by the challenges people face in accessing medical consultations due to distance or unfamiliarity with their surroundings. It connects patients with certified medical practitioners from various specialties whether globally or locally enabling virtual consultations and seamless scheduling of in-person checkups.

The platform is designed to simplify the process of finding trusted healthcare professionals, especially in unfamiliar regions, and places a strong emphasis on ease of use and a smooth experience for both patients and medical practitioners
</p>

## 🔋 Features
- 👉 **User**: This encapsulates all the users of the platform of which include the `patients`, `admins`,`super_admins` and certified `consultants`.
- 👉 **Registration**: This is the conversion of a guest to an authenticated user. The guest can be converted to a `PATIENT` or a `CONSULTANT` at a time. To qualify as a verified `CONSULTANT`, the `ADMIN` assigned must verify the authenticity of the certification, or in later versions can be verified by artificial intelligence.
- 👉 **Login**: This logs in the users into the platform. It uses JWT Authentication to verify user identity. It makes the feeds and data displayed to them personalized according to their recent activities or authorization level.
- 👉 **Recommendation**: This is unique to the `patients`. It recommends consultants or articles according to search patterns, location or recent activities.
- 👉 **Real Time Consultation and Chat**: This involves real time consultation/chats with consultants whom the admin has started a consultation with. It utilizes web sockets. The admin can have a real-time conversation with the practitioner of their choosing about topics they are interested in.
- 👉 **Admin Registration**: An `ADMIN` can be registered as an admin by a `SUPER_ADMIN` and an automatically generated password is assigned to that admin.
- 👉 **Consultant Certification**: Admins are assigned the certificates of the consultants that haven't been certified. Once the assigned admin certifies the certificates. The Consultant is automatically verified.
- 👉 **File upload**: The Consultant can upload pdf documents or clear images of their certificates. Everyone writer and patients need to upload their profile pictures for visual identification.
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
- [Docker Compose](#installing-docker-compose)
- [PostMan](https://www.postman.com)


#### Installing docker and docker-compose
- Set up Docker's `apt` repository.
```bash
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  jammy stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```
if you use an Ubuntu derivative distro, such as Linux Mint, you may need to use `UBUNTU_CODENAME` instead of `VERSION_CODENAME`.

- Install the Docker packages
```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```
- Verify that the Docker engine installation was successful
```bash
sudo docker run hello-world
```

- Update the package index, and install the latest version of Docker Compose
```bash
sudo apt-get update
sudo apt-get install docker-compose-plugin
```
- Finally, verify that the Docker compose is installed correctly by checking the version
```bash
docker compose version
```

🔰 **[For more info on installation in other distros](https://docs.docker.com/compose/install/linux/#install-using-the-repository)**

**Cloning the Repository**
```bash
git clone https://github.com/divjazz03/recommendic
```
**Set Up Environment Variables**

Create a new file named `.env` in the root of your project and add the following content
```env
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
SECRET={Make it as long as possible}
EMAIL_HOST=
EMAIL_PORT=
EMAIL_ID=
EMAIL_PASSWORD=
VERIFY_EMAIL_HOST=
```

**Build the app image**
```bash
docker build --tag=recommendic:latest
```

**Get it up and running**
```bash
docker compose up
``` 
**or**
```bash
docker-compose up --remove-ophans
```

**Now [localhost port 8080](http://localhost:8080) is ready to receive requests**
