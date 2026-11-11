# Online Store Backend API

Backend-система для интернет-магазина, разработанная на **Java 21** и **Spring Boot 3.3.5**.
Проект покрыт **API-автотестами** (RestAssured + JUnit 5) для демонстрации навыков Full Stack QA.

## 🚀 Стек технологий

- **Backend:** Java 21, Spring Boot 3.3.5, Spring Security (JWT), Spring Data JPA, Hibernate
- **Database:** PostgreSQL 18, Flyway (миграции)
- **Payment Integration:** Эмуляция ЮKassa (REST API, Webhooks, идемпотентность)
- **Email Notifications:** Асинхронная отправка через Spring Events (ConsoleEmailClient для демо)
- **Testing:** JUnit 5, RestAssured 5.4, Jackson
- **Build Tool:** Maven

## 📋 Функционал

- [x] Аутентификация и авторизация (JWT access/refresh токены)
- [x] Ролевая модель (USER, ADMIN)
- [x] CRUD товаров и категорий (Soft Delete, оптимистичная блокировка)
- [x] Корзина (ленивое создание, суммирование количества, расчет суммы)
- [x] Оформление заказа (транзакционность, списание остатков, фиксация цены)
- [x] Оплата (интеграция с платежным провайдером через интерфейс, обработка вебхуков)
- [x] Email-уведомления о смене статуса заказа (асинхронно, @TransactionalEventListener)
- [x] Смена статуса заказа администратором
- [x] Защита от брутфорса (Rate Limiting на /api/auth/login)
- [x] Единая обработка ошибок (GlobalExceptionHandler)

## 🧪 Автотесты

Тесты написаны с использованием паттернов **Request Object** и **Base Test**, что делает их читаемыми и поддерживаемыми.

**Покрытые модули:**
- `AuthTests` — регистрация, логин, валидация, rate limiting
- `CategoryTests` — CRUD, проверка прав ADMIN/USER
- `ProductTests` — CRUD, пагинация, валидация, soft delete
- `CartTests` — добавление, суммирование, удаление, доступ без токена
- `OrderTests` — создание из корзины, пустая корзина, IDOR (доступ к чужому заказу)
- `PaymentTests` — инициация оплаты, вебхуки, идемпотентность

**Запуск тестов:**

```bash
# 1. Запустить приложение (в отдельном терминале)
mvn spring-boot:run

# 2. Запустить все тесты
mvn test 
```
## ⚙️ Как запустить проект
1. Установите PostgreSQL и создайте БД online_store.
2. Настройте подключение в src/main/resources/application.yml.
3. Выполните: mvn spring-boot:run
4. Приложение доступно на http://localhost:8080.

## 📂 Структура проекта

```text
src/
├── main/java/com/dp/onlinestore/
│   ├── config/       # Security, JWT, RateLimiting
│   ├── controller/   # REST API
│   ├── dto/          # Data Transfer Objects
│   ├── entity/       # JPA Entities
│   ├── exception/    # Global Exception Handler
│   ├── integration/  # PaymentClient, NotificationClient (внешние интеграции)
│   ├── repository/   # Spring Data Repositories
│   └── service/      # Бизнес-логика
├── main/resources/
│   └── db/migration/ # Flyway миграции (V1-V5)
└── test/java/com/dp/onlinestore/api/
    ├── base/         # BaseTest
    ├── requests/     # Request Objects
    ├── tests/        # Тестовые классы
    └── utils/        # AuthHelper, TestDataGenerator
```

## 🛡️ Безопасность
Пароли хешируются (BCrypt).
JWT токены (stateless-сессии).
Защита от IDOR (проверка владения ресурсом).

Валидация входящих данных (Bean Validation).

Rate Limiting для защиты от брутфорса.