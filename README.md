# booking-service

# 🏢 Сервис бронирования мест

Веб-приложение для бронирования рабочих мест в коворкингах.  
Состоит из **React SPA (frontend)**, **Java Spring Boot (backend)** и **Golang** authorization service.  

---

## 🚀 Функционал
- Регистрация и авторизация пользователей с JWT-токенами  
- Создание, просмотр, редактирование и удаление бронирований  
- Страница «Мои брони» с фильтрацией  
- Админ-панель:
  - управление пользователями  
  - управление бронированиями  
  - управление коворкингами  

---

## 🛠️ Технологии
- **Frontend:** React, Vite  
- **Backend:** Java Spring Boot (REST + gRPC), Golang
- **Database:** PostgreSQL  
- **Infra:** Docker Compose  

---

## ⚡ Запуск

Требуется установленный **Docker** и **Docker Compose**.  

Выполните в корне проекта (где лежит `docker-compose.yml`):

```bash
docker compose up --build -d
```

---

После сборки будут доступны сервисы:

🌐 Frontend: http://localhost:3000

⚙️ Backend (Java Spring REST API): localhost:8080

🗄 PostgreSQL: localhost:5432
