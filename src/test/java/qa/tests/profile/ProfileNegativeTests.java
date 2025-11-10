package qa.tests.profile;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.config.Config;
import qa.tests.BaseTest;
import qa.utils.MsisdnGenerator;

/**
 * Негативные тесты для операций с профилями.
 * Проверяют валидацию данных и обработку ошибок.
 */
public class ProfileNegativeTests extends BaseTest {

    /**
     * Тест: создание профиля с невалидным MSISDN (слишком короткий).
     * 
     * Формат MSISDN: 996XXXXXXXXX (12 цифр)
     * "123456" - всего 6 цифр
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingProfileWithInvalidMsisdn() {
        String invalidMsisdn = "123456";
        
        Response response = profileApi.createProfile(invalidMsisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: создание профиля с неверным префиксом.
     * 
     * MSISDN должен начинаться с 99680, а не 99680x
     * "996801234567" - 12 цифр, но 99680x вместо 99680
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingProfileWithWrongPrefix() {
        String invalidMsisdn = "996801234567";
        
        Response response = profileApi.createProfile(invalidMsisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: создание профиля с MSISDN меньше 12 цифр.
     * 
     * "99680123456" - 11 цифр вместо 12
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingProfileWithTooShortMsisdn() {
        String invalidMsisdn = "99680123456";
        
        Response response = profileApi.createProfile(invalidMsisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: создание профиля с MSISDN больше 12 цифр.
     * 
     * "9968012345678" - 13 цифр вместо 12
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingProfileWithTooLongMsisdn() {
        String invalidMsisdn = "9968012345678";
        
        Response response = profileApi.createProfile(invalidMsisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: создание дубликата профиля (повторный MSISDN).
     * 
     * Сценарий:
     * 1. Создаём профиль с MSISDN
     * 2. Пытаемся создать еще один с тем же MSISDN
     * 
     * MSISDN должен быть уникальным в системе.
     * Ожидаемый результат: второй запрос возвращает 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingDuplicateProfile() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        
        Response firstResponse = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        assertResponseIsCreated(firstResponse);
        
        Response duplicateResponse = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(duplicateResponse);
    }

    /**
     * Тест: создание профиля с null MSISDN.
     * 
     * Проверяет обязательность поля msisdn.
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenCreatingProfileWithNullMsisdn() {
        Response response = profileApi.createProfile(null, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: создание профиля с несуществующим pricePlanId.
     * 
     * pricePlanId должен существовать в таблице price_plans.
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenCreatingProfileWithInvalidPricePlanId() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        Long invalidPricePlanId = 999999L;
        
        Response response = profileApi.createProfile(msisdn, 
                invalidPricePlanId, Config.DEFAULT_USER_ID);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: создание профиля с несуществующим userId.
     * 
     * userId должен существовать в таблице users.
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenCreatingProfileWithInvalidUserId() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        Long invalidUserId = 999999L;
        
        Response response = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, invalidUserId);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: получение несуществующего профиля по ID.
     * GET /api/profile/999999
     * 
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenGettingNonExistentProfileById() {
        Long nonExistentId = 999999L;
        
        Response response = profileApi.getProfileById(nonExistentId);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: получение профиля по несуществующему MSISDN.
     * GET /api/profile?msisdn=996801111111
     * 
     * ВНИМАНИЕ: это странное поведение API - возвращает 200 вместо 404!
     * Ожидаемый результат: 200 OK (возможно баг API)
     */
    @Test
    public void shouldReturn200WhenGettingNonExistentProfileByMsisdn() {
        String nonExistentMsisdn = "996801111111";
        
        Response response = profileApi.getProfileByMsisdn(nonExistentMsisdn);
        
        assertResponseIsOk(response);
    }

    /**
     * Тест: обновление несуществующего профиля.
     * PUT /api/profile/999999
     * 
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenUpdatingNonExistentProfile() {
        Long nonExistentId = 999999L;
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        
        Response response = profileApi.updateProfile(nonExistentId, msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: обновление профиля с невалидным MSISDN.
     * 
     * Предусловие: создаём валидный профиль
     * Попытка: обновляем его с невалидным MSISDN "123"
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenUpdatingProfileWithInvalidMsisdn() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        Response createResponse = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        Long profileId = extractIdFromResponse(createResponse);
        
        String invalidMsisdn = "123";
        Response updateResponse = profileApi.updateProfile(profileId, invalidMsisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsBadRequest(updateResponse);
    }

    /**
     * Тест: удаление несуществующего профиля.
     * DELETE /api/profile/999999
     * 
     * ВНИМАНИЕ: API возвращает 204 NO_CONTENT даже для несуществующего профиля!
     * Это идемпотентное поведение DELETE (повторный запрос = тот же результат).
     * 
     * Ожидаемый результат: 204 NO_CONTENT
     */
    @Test
    public void shouldReturn204WhenDeletingNonExistentProfile() {
        Long nonExistentId = 999999L;
        
        Response response = profileApi.deleteProfile(nonExistentId);
        
        assertStatusCode(response, 204);
    }

    /**
     * Тест: повторное удаление уже удалённого профиля.
     * 
     * Сценарий:
     * 1. Создаём профиль
     * 2. Удаляем его (soft delete)
     * 3. Пытаемся удалить еще раз
     * 
     * ВНИМАНИЕ: API возвращает 204 NO_CONTENT (идемпотентность).
     * Ожидаемый результат: 204 NO_CONTENT
     */
    @Test
    public void shouldReturn204WhenDeletingAlreadyDeletedProfile() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        Response createResponse = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        Long profileId = extractIdFromResponse(createResponse);
        
        profileApi.deleteProfile(profileId);
        Response secondDeleteResponse = profileApi.deleteProfile(profileId);
        
        assertStatusCode(secondDeleteResponse, 204);
    }
}
