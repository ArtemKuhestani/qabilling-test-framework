package qa.tests.profile;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.config.Config;
import qa.dto.response.ProfileResponse;
import qa.tests.BaseTest;
import qa.utils.MsisdnGenerator;

import static org.testng.Assert.*;

/**
 * Позитивные тесты для операций с профилями.
 * Проверяют успешные CRUD операции через Profile API.
 */
public class ProfilePositiveTests extends BaseTest {

    /**
     * Тест: получение списка всех профилей.
     * GET /api/profile
     * 
     * Ожидаемый результат: 200 OK, в ответе массив content
     */
    @Test
    public void shouldReturnAllProfiles() {
        Response response = profileApi.getAllProfiles();
        
        assertResponseIsOk(response);
        assertNotNull(response.jsonPath().getList("content"));
    }

    /**
     * Тест: получение списка удалённых профилей.
     * GET /api/profile?removed=true
     * 
     * Проверяет фильтрацию по soft-delete флагу.
     * Ожидаемый результат: 200 OK, массив removed профилей
     */
    @Test
    public void shouldReturnAllRemovedProfiles() {
        Response response = profileApi.getAllRemovedProfiles();
        
        assertResponseIsOk(response);
        assertNotNull(response.jsonPath().getList("content"));
    }

    /**
     * Тест: создание профиля с валидными данными.
     * POST /api/profile
     * 
     * Проверки:
     * - 201 CREATED
     * - Сгенерирован ID
     * - MSISDN совпадает с отправленным
     * - pricePlanId и userId совпадают с дефолтными
     */
    @Test
    public void shouldCreateProfileWithValidData() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        
        Response response = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsCreated(response);
        ProfileResponse profile = extractProfileFromResponse(response);
        
        assertNotNull(profile.getId());
        assertProfileMsisdn(profile, msisdn);
        assertEquals(profile.getPricePlanId(), Config.DEFAULT_PRICE_PLAN_ID);
        assertEquals(profile.getUserId(), Config.DEFAULT_USER_ID);
    }

    /**
     * Тест: получение профиля по существующему ID.
     * GET /api/profile/{id}
     * 
     * Предусловие: создаём профиль
     * Проверки: 200 OK, ID совпадает
     */
    @Test
    public void shouldGetProfileByExistingId() {
        Long profileId = createProfileAndReturnId();
        
        Response response = profileApi.getProfileById(profileId);
        
        assertResponseIsOk(response);
        ProfileResponse profile = extractProfileFromResponse(response);
        assertEquals(profile.getId(), profileId);
    }

    /**
     * Тест: получение профиля по существующему MSISDN.
     * GET /api/profile?msisdn={msisdn}
     * 
     * Предусловие: создаём профиль с известным MSISDN
     * Проверки: 200 OK, MSISDN совпадает
     */
    @Test
    public void shouldGetProfileByExistingMsisdn() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        createProfile(msisdn);
        
        Response response = profileApi.getProfileByMsisdn(msisdn);
        
        assertResponseIsOk(response);
        ProfileResponse profile = extractProfileFromResponse(response);
        assertProfileMsisdn(profile, msisdn);
    }

    /**
     * Тест: обновление профиля.
     * PUT /api/profile/{id}
     * 
     * Сценарий:
     * 1. Создаём профиль с оригинальным MSISDN
     * 2. Обновляем на новый MSISDN и pricePlanId
     * 3. Получаем профиль и проверяем изменения
     */
    @Test
    public void shouldUpdateProfileSuccessfully() {
        String originalMsisdn = MsisdnGenerator.generateUniqueMsisdn();
        Long profileId = createProfileAndReturnId(originalMsisdn);
        
        String newMsisdn = MsisdnGenerator.generateUniqueMsisdn();
        Long newPricePlanId = 1L;
        
        Response updateResponse = profileApi.updateProfile(profileId, newMsisdn, 
                newPricePlanId, Config.DEFAULT_USER_ID);
        assertResponseIsOk(updateResponse);
        
        Response getResponse = profileApi.getProfileById(profileId);
        ProfileResponse updated = extractProfileFromResponse(getResponse);
        
        assertProfileMsisdn(updated, newMsisdn);
        assertEquals(updated.getPricePlanId(), newPricePlanId);
    }

    /**
     * Тест: удаление профиля (soft delete).
     * DELETE /api/profile/{id}
     * 
     * ВАЖНО: это soft delete - профиль помечается removed=true
     * 
     * Проверки:
     * - DELETE возвращает 200
     * - Последующий GET по ID возвращает 404
     */
    @Test
    public void shouldDeleteProfileSuccessfully() {
        Long profileId = createProfileAndReturnId();
        
        Response deleteResponse = profileApi.deleteProfile(profileId);
        assertStatusCode(deleteResponse, 200);
        
        Response getResponse = profileApi.getProfileById(profileId);
        assertStatusCode(getResponse, 404);
    }

    /**
     * Тест: создание нескольких профилей для одного пользователя.
     * 
     * Проверяет, что один userId может иметь несколько профилей.
     * Ограничение только по уникальности MSISDN.
     */
    @Test
    public void shouldCreateMultipleProfilesForSameUser() {
        String msisdn1 = MsisdnGenerator.generateUniqueMsisdn();
        String msisdn2 = MsisdnGenerator.generateUniqueMsisdn();
        
        Response response1 = profileApi.createProfile(msisdn1, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        Response response2 = profileApi.createProfile(msisdn2, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        
        assertResponseIsCreated(response1);
        assertResponseIsCreated(response2);
        
        ProfileResponse profile1 = extractProfileFromResponse(response1);
        ProfileResponse profile2 = extractProfileFromResponse(response2);
        
        assertNotEquals(profile1.getId(), profile2.getId());
        assertProfileMsisdn(profile1, msisdn1);
        assertProfileMsisdn(profile2, msisdn2);
    }

    /**
     * Вспомогательный метод: создаёт профиль с уникальным MSISDN.
     * @return ID созданного профиля
     */
    private Long createProfileAndReturnId() {
        return createProfileAndReturnId(MsisdnGenerator.generateUniqueMsisdn());
    }

    /**
     * Вспомогательный метод: создаёт профиль с указанным MSISDN.
     * @param msisdn телефонный номер
     * @return ID созданного профиля
     */
    private Long createProfileAndReturnId(String msisdn) {
        Response response = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        ProfileResponse profile = extractProfileFromResponse(response);
        return profile.getId();
    }

    /**
     * Вспомогательный метод: создаёт профиль без возврата ID.
     * @param msisdn телефонный номер
     */
    private void createProfile(String msisdn) {
        profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
    }
}
