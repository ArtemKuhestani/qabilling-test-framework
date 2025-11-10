package qa.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.config.Config;

import static io.restassured.RestAssured.given;

/**
 * API клиент для работы со счетчиками ресурсов абонентов.
 * 
 * Counter (счетчик) хранит остатки ресурсов по тарифному плану:
 * - megabytes: остаток интернет-трафика в мегабайтах
 * - seconds: остаток минут разговора (хранится в секундах)
 * - sms: остаток SMS сообщений
 * 
 * Счетчик создается автоматически при создании профиля.
 * Начальные значения берутся из выбранного тарифного плана.
 * 
 * Доступные операции:
 * - Просмотр всех счетчиков
 * - Просмотр активных счетчиков (где есть неизрасходованные ресурсы)
 * - Просмотр счетчика конкретного профиля
 * 
 * Примечание: Этот клиент предоставляет только чтение (GET запросы).
 * Обновление ресурсов происходит через USER эндпоинты (useMegabytes, useSeconds, useSms).
 */
public class CounterApi {

    /**
     * Спецификация REST Assured с настроенной авторизацией.
     * Передается в конструктор при создании клиента.
     */
    private final RequestSpecification spec;

    /**
     * Конструктор API клиента для счетчиков.
     * 
     * @param spec настроенная спецификация с JWT токеном и базовым URL
     */
    public CounterApi(RequestSpecification spec) {
        this.spec = spec;
    }

    // ==================== GET ЗАПРОСЫ (ТОЛЬКО ЧТЕНИЕ) ====================

    /**
     * Получить список всех счетчиков в системе.
     * 
     * HTTP: GET /api/admin/counter/all
     * 
     * @return Response с массивом всех счетчиков
     * 
     * Структура ответа:
     * {
     *   "code": "OK",
     *   "content": [
     *     {
     *       "id": 1,
     *       "profileId": 1,
     *       "megabytes": 5000,
     *       "seconds": 180000,
     *       "sms": 100
     *     },
     *     ...
     *   ]
     * }
     * 
     * Каждый счетчик содержит:
     * - id: уникальный идентификатор (совпадает с profileId)
     * - profileId: ID профиля-владельца
     * - megabytes: остаток интернета в МБ
     * - seconds: остаток минут в секундах (180000 сек = 3000 мин = 50 часов)
     * - sms: остаток SMS
     */
    public Response getAllCounters() {
        return given()
                .spec(spec)
                .when()
                .get(Config.COUNTER_ALL);
    }

    /**
     * Получить список активных счетчиков.
     * 
     * HTTP: GET /api/admin/counter/all-active
     * 
     * @return Response с массивом активных счетчиков
     * 
     * Активный счетчик = хотя бы один ресурс больше нуля.
     * Если megabytes=0, seconds=0, sms=0, то счетчик неактивен и не попадет в список.
     * 
     * Используется для:
     * - Мониторинга: сколько абонентов еще имеют остатки
     * - Статистики: какие ресурсы наиболее востребованы
     */
    public Response getAllActiveCounters() {
        return given()
                .spec(spec)
                .when()
                .get(Config.COUNTER_ALL_ACTIVE);
    }

    /**
     * Получить счетчик конкретного профиля по ID.
     * 
     * HTTP: GET /api/admin/counter/{id}
     * 
     * @param id идентификатор профиля (совпадает с id счетчика)
     * @return Response с данными счетчика или 404 если не найден
     * 
     * Пример:
     * Response response = counterApi.getCounterById(123L);
     * CounterResponse counter = response.jsonPath().getObject("content", CounterResponse.class);
     * Long remainingMB = counter.getMegabytes(); // сколько МБ осталось
     */
    public Response getCounterById(Long id) {
        return given()
                .spec(spec)
                .when()
                .get(Config.COUNTER_BY_ID + id);
    }
}
