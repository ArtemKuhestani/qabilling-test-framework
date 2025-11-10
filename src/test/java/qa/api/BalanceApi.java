package qa.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.config.Config;
import qa.dto.request.UpdateBalanceRequest;

import static io.restassured.RestAssured.given;

/**
 * API клиент для работы с балансами абонентов.
 * 
 * Баланс хранит денежную сумму на счету абонента.
 * Баланс автоматически создается при создании профиля с начальным значением 0.
 * 
 * Доступные операции:
 * - Просмотр всех балансов
 * - Просмотр баланса конкретного профиля
 * - Обновление суммы на балансе
 * 
 * Примечание: У каждого профиля может быть только один баланс.
 * ID баланса соответствует ID профиля.
 */
public class BalanceApi {

    /**
     * Спецификация REST Assured с настроенной авторизацией.
     * Используется во всех HTTP запросах этого клиента.
     */
    private final RequestSpecification spec;

    /**
     * Конструктор API клиента для балансов.
     * 
     * @param spec настроенная спецификация с JWT токеном и базовым URL
     */
    public BalanceApi(RequestSpecification spec) {
        this.spec = spec;
    }

    // ==================== GET ЗАПРОСЫ (ЧТЕНИЕ) ====================

    /**
     * Получить список всех балансов в системе.
     * 
     * HTTP: GET /api/balance/all
     * 
     * @return Response с массивом балансов в JSON
     * 
     * Структура ответа:
     * {
     *   "code": "OK",
     *   "content": [
     *     {"id": 1, "profileId": 1, "amount": 1000.50},
     *     {"id": 2, "profileId": 2, "amount": 500.00}
     *   ]
     * }
     * 
     * Каждый баланс содержит:
     * - id: уникальный идентификатор баланса (совпадает с profileId)
     * - profileId: ID профиля-владельца
     * - amount: текущая сумма на балансе (Double)
     */
    public Response getAllBalances() {
        return given()
                .spec(spec)
                .when()
                .get(Config.BALANCE_ALL);
    }

    /**
     * Получить баланс конкретного профиля по ID.
     * 
     * HTTP: GET /api/balance/{id}
     * 
     * @param id идентификатор профиля (совпадает с id баланса)
     * @return Response с данными баланса или 404 если профиль не найден
     * 
     * Пример:
     * Response response = balanceApi.getBalanceById(123L);
     * BalanceResponse balance = response.jsonPath().getObject("content", BalanceResponse.class);
     * Double amount = balance.getAmount(); // получаем текущую сумму
     */
    public Response getBalanceById(Long id) {
        return given()
                .spec(spec)
                .when()
                .get(Config.BALANCE_BY_ID + id);
    }

    // ==================== PUT ЗАПРОС (ОБНОВЛЕНИЕ) ====================

    /**
     * Обновить сумму на балансе используя готовый объект запроса.
     * 
     * HTTP: PUT /api/balance/update/{id}
     * Body: {"amount": 1500.00}
     * 
     * @param id идентификатор профиля/баланса для обновления
     * @param request объект UpdateBalanceRequest с новой суммой
     * @return Response с обновленным балансом
     * 
     * ВАЖНО: Метод ЗАМЕНЯЕТ текущую сумму на новую, а не добавляет к существующей!
     * Если баланс был 1000, и вы отправите {"amount": 500}, баланс станет 500, а не 1500.
     * 
     * Для пополнения/списания используйте отдельные эндпоинты credit/debit (не реализованы в этом клиенте).
     */
    public Response updateBalance(Long id, UpdateBalanceRequest request) {
        return given()
                .spec(spec)
                .body(request)
                .when()
                .put(Config.BALANCE_UPDATE + id);
    }

    /**
     * Обновить сумму на балансе используя конкретное значение.
     * Удобная перегрузка для простых случаев.
     * 
     * @param id идентификатор профиля/баланса
     * @param amount новая сумма баланса (Double)
     * @return Response с обновленным балансом
     * 
     * Метод создает UpdateBalanceRequest через Builder и вызывает основной updateBalance.
     * 
     * Пример:
     * Response response = balanceApi.updateBalance(123L, 1500.0);
     * // Баланс профиля 123 теперь равен 1500.0
     */
    public Response updateBalance(Long id, Double amount) {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .amount(amount)
                .build();
        return updateBalance(id, request);
    }
}
