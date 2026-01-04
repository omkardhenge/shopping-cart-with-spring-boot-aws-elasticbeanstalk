# 🛒 E-Commerce Web Application delpoyed on Cloud
### Spring Boot | AWS Elastic Beanstalk | MySQL RDS

A **production-style full-stack e-commerce web application** built using **Spring Boot** and deployed on **AWS Elastic Beanstalk**.  
This project demonstrates **frontend and backend engineering, security implementation, ORM handling, Thymeleaf MVC flow, and real AWS deployment debugging**.

---
## Website Screenshots

### Login Page
![Login Page](screenshots/login.png "Login Page")

### Home Page
![Home Page](screenshots/home.png "Home Page")

### Admin Dashboard
![Admin Dashboard](screenshots/admin-dashboard.png "Admin Dashboard")

### User Profile
![User Profile](screenshots/user-profile.png "User Profile")

---

## 🎯 Project Objective

To design and deploy a **secure, scalable e-commerce system** while handling **real-world Spring Boot and AWS issues**, including:
- Hibernate mapping failures
- Thymeleaf template rendering errors
- MySQL connection timeouts
- AWS 500 / 504 / 404 production errors

---

## 🧰 Tech Stack

### Backend
- Spring Boot
- Spring MVC
- Spring Security
- JPA & Hibernate
- Servlets
- BCrypt Password Encoder

### Frontend
- HTML5
- CSS3
- jQuery
- Bootstrap
- Thymeleaf

### Database
- MySQL
- AWS RDS (hosting platform)

### Cloud & Deployment
- AWS Elastic Beanstalk
- AWS EC2
- AWS S3 (profile images)
- Maven

---

## 🔐 Security Implementation

- Password encryption using **BCrypt**
- Account lock after **3 failed login attempts**
- Lock duration: **3000 seconds**
- Role-based authorization (Admin / User)
- Secured admin routes via Spring Security filter chain

---

## ▶️ Build & Run Locally

```bash
https://localhost:8080
```

```bash
git clone https:///shopping-cart-with-spring-boot-aws-elasticbeanstalk.git
cd shopping-cart-with-spring-boot-aws-elasticbeanstalk
mvn clean package 
mvn spring-boot:run
```


## ▶️ Build & Run on cloud

```bash
https://your_env_name.your_region.elasticbeanstalk.com
```

## 🧑‍💼 Admin Features

- Admin dashboard (secured routes)
- Category management (Add / Update)
- Product management
- Order status update
- Automatic **email notification** to customer on order status change

> **Note:**  
> One admin account is hardcoded **only for testing purposes**.  
> Architecture allows easy extension to add admin creation via dashboard.

---

## 👤 User Features

- User registration & authentication
- Product browsing by category
- Cart & order placement
- Payment page flow
- Email notifications for order updates

---

## 🧪 Errors Faced & Debugged (Real-World)

This project involved **actual production-level debugging**, not simulated errors.

### 🔴 Spring Boot / Hibernate Errors
| Issue | Root Cause |
|-----|-----------|
| `MappingException` | Incorrect entity relationships |
| `LazyInitializationException` | Lazy loading outside Hibernate session |
| Schema mismatch | `ddl-auto` conflicts between local & RDS |
| Bean creation failure | Circular dependencies / wrong injection |
| JPQL errors | Repository method naming & syntax mistakes |

### 🔴 Thymeleaf / MVC Errors
| Issue | Root Cause |
|-----|-----------|
| Template not found | Incorrect folder structure |
| 404 on valid pages | Controller mapping mismatch |
| 500 error | Missing model attributes |
| Fragment failure | Incorrect Thymeleaf layout configuration |

### 🔴 Database / AWS Errors
| Error | Cause |
|-----|------|
| MySQL connection timeout | RDS security group misconfiguration |
| 500 Internal Server Error | DB pool exhaustion / mapping issues |
| 504 Gateway Timeout | Elastic Beanstalk health check timeout |
| 404 Error | Context path & routing issues |

---

