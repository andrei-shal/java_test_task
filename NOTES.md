За первую неделю в кодовой базе я бы взялся за три вещи.
Первое - вынести магические константы в конфиг: `MAX_POINTS = 1000` в `TripServiceImpl` и флаг сидирования в `MongoSeedLoader` должны лежать в `application.yml` с `@Value`, а не быть зашитыми в код.

Второе - убрать дублирование маппинга `TelemetryEvent -> TelemetryPoint`, который скопирован между `TelemetryConsumer` и `MongoSeedLoader`.
В обоих файлах вручную копируются поля (`vehicleId`, `ts`, `lat`, `lon`, `speed`, `ignition`) идентичным блоком кода.
Если добавится новое поле - его забудут поправить в одном из двух мест. Нужно вынести в общий маппер или фабричный метод.

Третье - если MongoDB недоступна, `findRecentPoints()` кинет `DataAccessResourceFailureException`, который вылетит из контроллера как 500 Internal Server Error без понятного сообщения.
Стоит добавить обработку в `TripServiceImpl` (или `GlobalExceptionHandler`), чтобы в такой ситуации возвращался 503 Service Unavailable с внятным описанием проблемы.
