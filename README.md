```
   ____  _             _
  |  _ \(_)_ __   __ _(_)_ __   ___
  | |_) | | '_ \ / _` | | '_ \ / _ \
  |  __/| | | | | (_| | | | | |  __/
  |_|   |_|_| |_|\__, |_|_| |_|\___|
                 |___/   F L E E T   P U L S E
```

# Pingine Fleet Pulse — тестовое задание

Привет! Это тестовое задание для позиции Java-разработчика в команду Pingine.

Pingine — европейская облачная платформа для GPS-трекинга и управления коммерческим транспортом. Сервис `fleet-pulse`, который ты держишь в руках, отвечает за приём телеметрии с машин и расчёт поездок для дашборда диспетчеров.

---

## Что в проекте уже есть

Сервис почти полностью собран. Тебе достаются:

- REST API на Spring Web + Swagger UI
- Метаданные транспорта в PostgreSQL (через Spring Data JPA + Flyway)
- Сырые точки телеметрии в MongoDB
- Кэш справочника машин в Redis
- Приём событий из RabbitMQ (`vehicle.telemetry.v1`)
- Интеграция с соседним сервисом `vehicle-registry` через Feign
- Примеры тестов: интеграционный с Testcontainers и WireMock-стаб
- `docker-compose.yml` со всеми зависимостями
- Postman-коллекция

---

## Что нужно сделать

Реализовать **2 обязательных пункта** + 1 опциональный (бонус). Ориентир по времени: 1.5-2 часа на обязательные, ещё 30-40 мин на бонус.

### 1. Детектор поездок

Файл: `src/main/java/com/pingine/fleetpulse/service/trip/TripDetector.java`.

Метод `detect(List<TelemetryPoint>)` должен разбить поток точек телеметрии на завершённые поездки. Поездка — это интервал от точки с `ignition=true` до следующей точки с `ignition=false`. Контракт описан в JavaDoc интерфейса `TripDetector`.

В файле `src/test/java/com/pingine/fleetpulse/service/trip/TripDetectorTest.java` лежат два теста с аннотацией `@Disabled`. Сними её и доведи оба до зелёного.

### 2. Endpoint `GET /api/v1/vehicles/{id}/last-trip`

Файл: `src/main/java/com/pingine/fleetpulse/service/TripServiceImpl.java`.

Контроллер и DTO `TripResponse` готовы. Сервисный метод `getLastTrip` — заглушка. Реализуй:

- Достань точки телеметрии для машины из MongoDB
- Прогоняй их через `TripDetector` и возьми последнюю завершённую поездку
- Метаданные машины (модель, VIN, имя водителя) добавь, обратившись к `VehicleService.getById(...)`
- Собери и верни `TripResponse`

Если поездок нет — верни 404 (можно бросить `VehicleNotFoundException` или завести своё исключение).

### 3. (Опционально) MockMvc-тест на endpoint

**Бонусный пункт.** Если останется время и есть желание — напиши один тест через `@WebMvcTest` + `@MockBean`. Проверь, что endpoint возвращает корректный JSON для существующей машины с поездкой и 404, если поездок нет.

Положи в `src/test/java/com/pingine/fleetpulse/api/TripControllerTest.java`.

Если не делаешь — ничего страшного, обсудим этот тест на звонке.

---

## Как запустить

```bash
docker-compose up -d
mvn spring-boot:run
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- RabbitMQ Management: <http://localhost:15672> (user: `pingine`, password: `password`)

Сервис `vehicle-registry` отдельно поднимать не нужно. Endpoint `GET /vehicles/{id}` работает без него; endpoint `POST /vehicles/{id}/refresh` сделает `try/catch` и продолжит без обогащения.

При первом старте в MongoDB загружаются примеры точек телеметрии из `src/main/resources/seed/telemetry-sample.ndjson` — этого хватит, чтобы сразу проверить `GET /vehicles/{id}/last-trip` через Postman или Swagger. Если не нужно — выключи флагом `fleet-pulse.seed.enabled=false`.

Тесты:
```bash
mvn test
```

---

## Как сдать

Любой удобный способ:

- .patch файл с решением
- ссылка на репозиторий (GitHub / GitLab)
- zip с проектом


Не забудь приложить ответ на пункт ниже.

---

## Один абзац от тебя

В отдельном файле `NOTES.md` (или в письме) напиши **один абзац** в свободной форме: что бы ты изменил в этой кодовой базе, если бы пришёл в команду на постоянной основе после первой недели работы. Это часть оценки — нам важно понять, как ты смотришь на чужой код.

---

## Стек

Java 11 · Spring Boot 2.7 · Maven · PostgreSQL · MongoDB · RabbitMQ · Redis · Spring Cloud OpenFeign · JUnit 5 · Mockito · WireMock ·


Удачи!
