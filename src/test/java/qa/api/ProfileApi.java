package qa.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.config.Config;
import qa.dto.request.CreateProfileRequest;

import static io.restassured.RestAssured.given;

/**
 * API клиент для работы с профилями абонентов.
 * 
 * Инкапсулирует все HTTP операции с эндпоинтами профилей в одном месте.
 * Это реализация паттерна "Service Object" - вся бизнес-логика работы с API
 * вынесена в отдельный класс, тесты просто используют готовые методы.
 * 
 * Преимущества такого подхода:
 * - Тесты становятся читаемыми: profileApi.createProfile() вместо длинного REST Assured кода
 * - Изменения API требуют правки только в одном месте
 * - Переиспользование: один метод используется во многих тестах
 * 
 * Все методы возвращают Response - объект REST Assured с HTTP ответом.
 * Из Response можно получить: статус код, заголовки, JSON тело и т.д.
 */
public class ProfileApi {

    /**
     * Спецификация REST Assured с настроенной авторизацией и базовыми параметрами.
     * Передается в конструктор при создании API клиента.
     * 
     * final означает что после инициализации в конструкторе значение нельзя изменить.
     * Это гарантирует что все запросы используют одну и ту же авторизацию.
     */
    private final RequestSpecification spec;

    /**
     * Конструктор API клиента.
     * 
     * @param spec настроенная спецификация с токеном и базовым URL
     * 
     * Пример создания:
     * RequestSpecification spec = ... // создается в BaseTest
     * ProfileApi api = new ProfileApi(spec);
     */
    public ProfileApi(RequestSpecification spec) {
        this.spec = spec;
    }

    // ==================== GET ЗАПРОСЫ (ЧТЕНИЕ) ====================

    /**
     * Получить список всех профилей в системе.
     * 
     * HTTP: GET /api/admin/profile/all
     * 
     * @return Response с массивом профилей в JSON
     * 
     * Структура ответа:
     * {
     *   "code": "OK",
     *   "content": [
     *     {"id": 1, "msisdn": "996801111111", ...},
     *     {"id": 2, "msisdn": "996802222222", ...}
     *   ]
     * }
     */
    public Response getAllProfiles() {
        return given()
                .spec(spec)
                .when()
                .get(Config.PROFILE_ALL);
    }

    /**
     * Получить список всех удаленных профилей.
     * 
     * HTTP: GET /api/admin/profile/all-removed
     * 
     * В системе используется "мягкое удаление" (soft delete):
     * - Профиль не удаляется физически из БД
     * - Просто помечается флагом removed=true
     * - Этот метод возвращает такие помеченные профили
     * 
     * @return Response с массивом удаленных профилей
     */
    public Response getAllRemovedProfiles() {
        return given()
                .spec(spec)
                .when()
                .get(Config.PROFILE_ALL_REMOVED);
    }

    /**
     * Получить профиль по его уникальному идентификатору.
     * 
     * HTTP: GET /api/admin/profile/{id}
     * 
     * @param id уникальный идентификатор профиля (Long, например 123)
     * @return Response с данными профиля или 404 если не найден
     * 
     * Пример использования:
     * Response response = profileApi.getProfileById(123L);
     * // L в конце означает что это Long число
     */
    public Response getProfileById(Long id) {
        return given()
                .spec(spec)
                .when()
                .get(Config.PROFILE_BY_ID + id);
    }

    /**
     * Найти профиль по номеру телефона (MSISDN).
     * 
     * HTTP: GET /api/admin/profile/getByMsisdn/{msisdn}
     * 
     * @param msisdn номер телефона в формате "996801234567" (12 цифр)
     * @return Response с данными профиля или 200 с пустым content если не найден
     * 
     * MSISDN (Mobile Station International Subscriber Directory Number) -
     * это международный формат номера мобильного телефона.
     * Для Кыргызстана: 99680 + 7 цифр номера
     */
    public Response getProfileByMsisdn(String msisdn) {
        return given()
                .spec(spec)
                .when()
                .get(Config.PROFILE_BY_MSISDN + msisdn);
    }

    // ==================== POST ЗАПРОС (СОЗДАНИЕ) ====================

    /**
     * Создать новый профиль абонента используя готовый объект запроса.
     * 
     * HTTP: POST /api/admin/profile/create
     * Body: {"msisdn": "...", "pricePlanId": ..., "userId": ...}
     * 
     * @param request объект CreateProfileRequest с данными нового профиля
     * @return Response с созданным профилем (содержит присвоенный id)
     * 
     * При создании профиля автоматически создаются связанные сущности:
     * - Balance (баланс со значением 0)
     * - Counter (счетчик с ресурсами из тарифного плана)
     */
    public Response createProfile(CreateProfileRequest request) {
        return given()
                .spec(spec)
                .body(request)
                .when()
                .post(Config.PROFILE_CREATE);
    }

    /**
     * Создать новый профиль абонента используя отдельные параметры.
     * Удобная перегрузка метода createProfile для простых случаев.
     * 
     * @param msisdn номер телефона (12 цифр)
     * @param pricePlanId ID тарифного плана (обычно 1, 2, 3...)
     * @param userId ID пользователя-владельца профиля
     * @return Response с созданным профилем
     * 
     * Метод внутри создает CreateProfileRequest через Builder и вызывает основной createProfile.
     * 
     * Пример:
     * Response response = profileApi.createProfile("996801234567", 2L, 1L);
     */
    public Response createProfile(String msisdn, Long pricePlanId, Long userId) {
        CreateProfileRequest request = CreateProfileRequest.builder()
                .msisdn(msisdn)
                .pricePlanId(pricePlanId)
                .userId(userId)
                .build();
        return createProfile(request);
    }

    // ==================== PUT ЗАПРОС (ОБНОВЛЕНИЕ) ====================

    /**
     * Обновить существующий профиль используя готовый объект запроса.
     * 
     * HTTP: PUT /api/admin/profile/update/{id}
     * Body: {"msisdn": "...", "pricePlanId": ..., "userId": ...}
     * 
     * @param id идентификатор профиля для обновления
     * @param request объект CreateProfileRequest с новыми данными
     * @return Response с обновленным профилем
     * 
     * Примечание: используется тот же CreateProfileRequest что и для создания.
     * Это распространенная практика когда структура данных для создания и обновления одинаковая.
     */
    public Response updateProfile(Long id, CreateProfileRequest request) {
        return given()
                .spec(spec)
                .body(request)
                .when()
                .put(Config.PROFILE_UPDATE + id);
    }

    /**
     * Обновить существующий профиль используя отдельные параметры.
     * Удобная перегрузка для простых случаев.
     * 
     * @param id идентификатор обновляемого профиля
     * @param msisdn новый номер телефона
     * @param pricePlanId новый ID тарифного плана
     * @param userId новый ID пользователя-владельца
     * @return Response с обновленным профилем
     */
    public Response updateProfile(Long id, String msisdn, Long pricePlanId, Long userId) {
        CreateProfileRequest request = CreateProfileRequest.builder()
                .msisdn(msisdn)
                .pricePlanId(pricePlanId)
                .userId(userId)
                .build();
        return updateProfile(id, request);
    }

    // ==================== DELETE ЗАПРОСЫ (УДАЛЕНИЕ) ====================

    /**
     * Удалить профиль по ID (мягкое удаление).
     * 
     * HTTP: DELETE /api/admin/profile/delete/{id}
     * 
     * @param id идентификатор профиля для удаления
     * @return Response с результатом (обычно 204 No Content при успехе)
     * 
     * Это "мягкое удаление" (soft delete):
     * - Профиль остается в БД
     * - Устанавливается флаг removed=true
     * - Профиль перестает отображаться в обычных запросах
     * - Можно восстановить если понадобится
     */
    public Response deleteProfile(Long id) {
        return given()
                .spec(spec)
                .when()
                .delete(Config.PROFILE_DELETE + id);
    }

    /**
     * Удалить все профили сразу.
     * 
     * HTTP: DELETE /api/admin/profile/delete/all
     * 
     * @return Response с результатом операции
     * 
     * ВНИМАНИЕ: Этот метод удаляет ВСЕ профили в системе!
     * Используется в тестах для очистки данных после выполнения.
     * В production такой метод обычно отсутствует или доступен только суперадмину.
     */
    public Response deleteAllProfiles() {
        return given()
                .spec(spec)
                .when()
                .delete(Config.PROFILE_DELETE + "all");
    }
}
