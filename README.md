**Веб-сервис для подачи показаний счетчиков отопления, горячей и холодной воды**


**Сборка проекта:**

Склонируйте репозиторий: git clone https://github.com/HoryaFilya/Monitoring-Service.git

Перейдите в директорию проекта: cd Monitoring-Service

Перейдите на нужную ветку: git checkout task-3

Поднимите контейнер с PostgreSQL: docker-compose up


**Запуск проекта:**

Запустите приложение: ./gradlew jettyRun


**Технологии:**

Java 17
Docker
Lombok
JUnit 5, Mockito, AssertJ, Testcontainers
SQL и PostgreSQL
Liquibase
JDBC
Jackson
JWT
AspectJ
Jetty 11.0.17
Servlets


**Эндпоинты:**

1. Регистрация пользователя
POST /registration
{
    "username": "test",
    "password": "test"
}

2. Аутентификация пользователя
POST /login
{
    "username": "test",
    "password": "test"
}

3. Выход из аккаунта
POST /logout

4. Получение актуальных показаний счетчиков
GET /indications/actual/{id}

5. Получение истории подачи показаний счетчиков
GET /indications/history/{id}

6. Получение показаний счетчиков за конкретный месяц
GET /indications/{month_number}/{id}

7. Подача показаний
POST /indications
{
    "heating": 100,
    "hotWater": 200,
    "coldWater": 300
}

8. Получение аудита действий пользователей
GET /admin/audits

9. Получение истории подачи показаний счетчиков пользователей
GET /admin/indications


**telegram:** @MishaShaikh
