package qa.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import qa.api.BalanceApi;
import qa.api.CounterApi;
import qa.api.ProfileApi;
import qa.config.Config;
import qa.dto.response.BalanceResponse;
import qa.dto.response.CounterResponse;
import qa.dto.response.ProfileResponse;
import qa.helpers.AuthHelper;

import static org.testng.Assert.*;

/**
 * Базовый класс для всех тестовых классов фреймворка.
 * 
 * Предоставляет:
 * - Готовую авторизацию с JWT токеном администратора
 * - API клиенты для работы с профилями, балансами и счетчиками
 * - Вспомогательные методы для проверки HTTP ответов
 * - Утилиты для извлечения данных из JSON ответов
 * 
 * Все тестовые классы должны наследоваться от BaseTest,
 * чтобы получить доступ к этим возможностям.
 * 
 * Пример использования:
 * public class ProfileTests extends BaseTest {
 *     @Test
 *     public void testGetProfile() {
 *         Response response = profileApi.getAllProfiles();
 *         assertResponseIsOk(response);
 *     }
 * }
 */
public class BaseTest {

    // ==================== API КЛИЕНТЫ И СПЕЦИФИКАЦИИ ====================
    
    /**
     * Спецификация REST Assured с настроенной авторизацией.
     * Содержит:
     * - Базовый URL (Config.BASE_URL)
     * - Authorization header с JWT токеном
     * - Content-Type: application/json
     * 
     * Эта спецификация автоматически применяется ко всем запросам через API клиенты.
     */
    protected static RequestSpecification adminSpec;
    
    /**
     * API клиент для работы с профилями абонентов.
     * Инкапсулирует все HTTP операции с эндпоинтами профилей:
     * - Создание, чтение, обновление, удаление профилей
     * - Получение списков всех/удаленных профилей
     * - Поиск профиля по MSISDN
     */
    protected ProfileApi profileApi;
    
    /**
     * API клиент для работы с балансами абонентов.
     * Позволяет:
     * - Получать список всех балансов
     * - Получать баланс конкретного профиля
     * - Обновлять сумму на балансе
     */
    protected BalanceApi balanceApi;
    
    /**
     * API клиент для работы со счетчиками ресурсов.
     * Счетчики хранят остатки минут, SMS, мегабайт по тарифу.
     * Клиент позволяет получать информацию о счетчиках (только чтение).
     */
    protected CounterApi counterApi;

    // ==================== ИНИЦИАЛИЗАЦИЯ ====================
    
    /**
     * Метод инициализации, выполняется один раз перед запуском всех тестов в классе.
     * 
     * Последовательность действий:
     * 1. Получает JWT токен администратора через AuthHelper
     * 2. Создает RequestSpecification с этим токеном
     * 3. Инициализирует API клиенты (profileApi, balanceApi, counterApi)
     * 
     * После выполнения этого метода все тесты в классе могут использовать
     * готовые API клиенты с авторизацией.
     * 
     * Аннотация @BeforeClass означает, что метод запустится:
     * - Один раз перед всеми тестами класса
     * - Автоматически (не нужно вызывать вручную)
     */
    @BeforeClass
    public void setupBaseTest() {
        adminSpec = createSpecificationWithToken(AuthHelper.getToken());
        profileApi = new ProfileApi(adminSpec);
        balanceApi = new BalanceApi(adminSpec);
        counterApi = new CounterApi(adminSpec);
    }

    /**
     * Создает настроенную спецификацию для REST Assured с JWT токеном.
     * 
     * Спецификация - это набор предварительных настроек для HTTP запросов:
     * - Base URI: куда отправлять запросы
     * - Headers: какие заголовки добавлять
     * - Content Type: формат данных (JSON)
     * 
     * @param token JWT токен для авторизации (получается из AuthHelper)
     * @return настроенная RequestSpecification, готовая к использованию
     * 
     * Пример: если token = "eyJhbGc...", то каждый запрос будет содержать:
     * Authorization: Bearer eyJhbGc...
     */
    protected RequestSpecification createSpecificationWithToken(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

    // ==================== ПРОВЕРКА HTTP СТАТУСОВ ====================
    
    /**
     * Проверяет что HTTP статус код ответа совпадает с ожидаемым.
     * Если коды не совпадают - тест упадет с понятным сообщением.
     * 
     * @param response HTTP ответ от сервера (объект Response из REST Assured)
     * @param expectedStatusCode ожидаемый статус код (например: 200, 404, 400)
     * 
     * Примеры:
     * - assertStatusCode(response, 200) - ожидаем успех
     * - assertStatusCode(response, 404) - ожидаем "не найдено"
     * - assertStatusCode(response, 400) - ожидаем "ошибка в запросе"
     */
    protected void assertStatusCode(Response response, int expectedStatusCode) {
        assertEquals(response.statusCode(), expectedStatusCode,
                "Status code mismatch");
    }

    /**
     * Проверяет что запрос выполнен успешно (HTTP 200 OK).
     * Сокращенная версия assertStatusCode(response, 200).
     * 
     * @param response HTTP ответ от сервера
     * 
     * Использовать когда операция должна пройти успешно:
     * - Получение существующих данных
     * - Успешное обновление
     * - Успешное создание (если API возвращает 200 вместо 201)
     */
    protected void assertResponseIsOk(Response response) {
        assertStatusCode(response, 200);
    }

    /**
     * Проверяет что ресурс создан (HTTP 201 Created).
     * Используется после операций создания новых сущностей.
     * 
     * @param response HTTP ответ от сервера
     * 
     * Типичный сценарий:
     * Response response = profileApi.createProfile(request);
     * assertResponseIsCreated(response);
     */
    protected void assertResponseIsCreated(Response response) {
        assertStatusCode(response, 201);
    }

    /**
     * Проверяет что запрос содержит ошибку (HTTP 400 Bad Request).
     * Используется в негативных тестах - когда мы специально отправляем некорректные данные.
     * 
     * @param response HTTP ответ от сервера
     * 
     * Примеры негативных тестов:
     * - Создание профиля с невалидным MSISDN
     * - Обновление с отрицательной суммой баланса
     * - Попытка создать дубликат
     */
    protected void assertResponseIsBadRequest(Response response) {
        assertStatusCode(response, 400);
    }

    /**
     * Проверяет что ресурс не найден (HTTP 404 Not Found).
     * Используется когда запрашиваем несуществующие данные.
     * 
     * @param response HTTP ответ от сервера
     * 
     * Примеры:
     * - Получение профиля с несуществующим ID
     * - Поиск по несуществующему MSISDN
     */
    protected void assertResponseIsNotFound(Response response) {
        assertStatusCode(response, 404);
    }

    /**
     * Проверяет поле "code" в JSON ответе.
     * Многие эндпоинты возвращают структуру: {"code": "OK", "content": {...}}
     * 
     * @param response HTTP ответ от сервера
     * @param expectedCode ожидаемое значение поля "code" (например: "OK", "ERROR")
     * 
     * Пример JSON:
     * {
     *   "code": "OK",
     *   "content": {
     *     "id": 123,
     *     "msisdn": "996801234567"
     *   }
     * }
     * 
     * assertResponseCodeIs(response, "OK") - проверит что code = "OK"
     */
    protected void assertResponseCodeIs(Response response, String expectedCode) {
        String actualCode = response.jsonPath().getString("code");
        assertEquals(actualCode, expectedCode);
    }

    // ==================== ИЗВЛЕЧЕНИЕ ДАННЫХ ИЗ JSON ====================
    
    /**
     * Извлекает объект профиля из JSON ответа.
     * Ожидается структура: {"content": {"id": ..., "msisdn": ..., ...}}
     * 
     * @param response HTTP ответ от сервера
     * @return объект ProfileResponse с данными профиля
     * 
     * REST Assured автоматически преобразует JSON в Java объект (десериализация).
     * Например, JSON {"id": 123, "msisdn": "996801234567"} станет
     * объектом ProfileResponse с соответствующими полями.
     */
    protected ProfileResponse extractProfileFromResponse(Response response) {
        return response.jsonPath().getObject("content", ProfileResponse.class);
    }

    /**
     * Извлекает объект баланса из JSON ответа.
     * 
     * @param response HTTP ответ от сервера
     * @return объект BalanceResponse с данными баланса
     * 
     * Пример JSON:
     * {
     *   "content": {
     *     "id": 123,
     *     "profileId": 456,
     *     "amount": 1000.50
     *   }
     * }
     */
    protected BalanceResponse extractBalanceFromResponse(Response response) {
        return response.jsonPath().getObject("content", BalanceResponse.class);
    }

    /**
     * Извлекает объект счетчика из JSON ответа.
     * 
     * @param response HTTP ответ от сервера
     * @return объект CounterResponse с остатками ресурсов
     * 
     * CounterResponse содержит:
     * - megabytes: остаток мегабайт
     * - seconds: остаток минут (в секундах)
     * - sms: остаток SMS
     */
    protected CounterResponse extractCounterFromResponse(Response response) {
        return response.jsonPath().getObject("content", CounterResponse.class);
    }

    /**
     * Извлекает только ID из JSON ответа.
     * Удобно когда нужен только идентификатор созданной сущности.
     * 
     * @param response HTTP ответ от сервера
     * @return ID созданного объекта (Long)
     * 
     * Пример использования:
     * Response response = profileApi.createProfile(request);
     * Long profileId = extractIdFromResponse(response);
     * // Теперь можем использовать profileId для других операций
     */
    protected Long extractIdFromResponse(Response response) {
        return response.jsonPath().getLong("content.id");
    }

    // ==================== СПЕЦИФИЧНЫЕ ПРОВЕРКИ ====================
    
    /**
     * Проверяет что сумма на балансе соответствует ожидаемой.
     * Использует сравнение с точностью до 0.01 (2 знака после запятой).
     * 
     * @param balance объект баланса для проверки
     * @param expectedAmount ожидаемая сумма (Double)
     * 
     * Примеры:
     * - assertBalanceAmount(balance, 1000.0) - ожидаем ровно 1000
     * - assertBalanceAmount(balance, 0.0) - ожидаем нулевой баланс
     * 
     * Точность 0.01 означает что 1000.00 и 1000.009 будут считаться равными.
     */
    protected void assertBalanceAmount(BalanceResponse balance, Double expectedAmount) {
        assertEquals(balance.getAmount(), expectedAmount, 0.01,
                "Balance amount mismatch");
    }

    /**
     * Проверяет что номер телефона профиля соответствует ожидаемому.
     * 
     * @param profile объект профиля для проверки
     * @param expectedMsisdn ожидаемый номер телефона (String)
     * 
     * MSISDN - международный формат номера телефона (12 цифр для Кыргызстана).
     * Пример: "996801234567"
     */
    protected void assertProfileMsisdn(ProfileResponse profile, String expectedMsisdn) {
        assertEquals(profile.getMsisdn(), expectedMsisdn,
                "Profile MSISDN mismatch");
    }
}
