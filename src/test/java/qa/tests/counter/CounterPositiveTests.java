package qa.tests.counter;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.config.Config;
import qa.dto.response.CounterResponse;
import qa.dto.response.ProfileResponse;
import qa.tests.BaseTest;
import qa.utils.MsisdnGenerator;

import static org.testng.Assert.*;

/**
 * Позитивные тесты для операций с счётчиками.
 * Counter API - только read-only операции (GET).
 */
public class CounterPositiveTests extends BaseTest {

    /**
     * Тест: получение списка всех счётчиков.
     * GET /api/counter
     * 
     * Ожидаемый результат: 200 OK, в ответе массив content
     */
    @Test
    public void shouldReturnAllCounters() {
        Response response = counterApi.getAllCounters();
        
        assertResponseIsOk(response);
        assertNotNull(response.jsonPath().getList("content"));
    }

    /**
     * Тест: получение списка активных счётчиков.
     * GET /api/counter?status=ACTIVE
     * 
     * Активные счётчики = счётчики профилей со status="ACTIVE"
     * Ожидаемый результат: 200 OK, массив активных счётчиков
     */
    @Test
    public void shouldReturnAllActiveCounters() {
        Response response = counterApi.getAllActiveCounters();
        
        assertStatusCode(response, 200);
        assertNotNull(response.jsonPath().getList("content"));
    }

    /**
     * Тест: получение счётчика по существующему profileId.
     * GET /api/counter/{profileId}
     * 
     * Предусловие: создаём новый профиль (счётчик создаётся автоматически)
     * Проверки:
     * - 200 OK
     * - counter.id не null
     * - counter.profileId совпадает
     */
    @Test
    public void shouldReturnCounterByExistingProfileId() {
        Long profileId = createProfileAndReturnId();
        
        Response response = counterApi.getCounterById(profileId);
        
        assertResponseIsOk(response);
        CounterResponse counter = extractCounterFromResponse(response);
        
        assertNotNull(counter.getId());
        assertEquals(counter.getProfileId(), profileId);
    }

    /**
     * Тест: автоматическое создание счётчика при создании профиля.
     * 
     * ВАЖНО: счётчик создаётся автоматически при создании профиля!
     * Нет отдельного API для создания счётчиков.
     */
    @Test
    public void shouldCreateCounterWhenProfileIsCreated() {
        Long profileId = createProfileAndReturnId();
        
        Response response = counterApi.getCounterById(profileId);
        
        assertResponseIsOk(response);
        CounterResponse counter = extractCounterFromResponse(response);
        
        // Проверяем, что все поля счётчика инициализированы
        assertNotNull(counter.getMegabytes());
        assertNotNull(counter.getSeconds());
        assertNotNull(counter.getSms());
    }

    /**
     * Тест: проверка начальных значений счётчика.
     * 
     * Новый счётчик должен иметь неотрицательные значения ресурсов.
     * Проверяем граничные значения (>= 0).
     */
    @Test
    public void shouldHaveInitialCounterValuesForNewProfile() {
        Long profileId = createProfileAndReturnId();
        
        Response response = counterApi.getCounterById(profileId);
        CounterResponse counter = extractCounterFromResponse(response);
        
        assertTrue(counter.getMegabytes() >= 0);
        assertTrue(counter.getSeconds() >= 0);
        assertTrue(counter.getSms() >= 0);
    }

    /**
     * Вспомогательный метод: создаёт профиль и возвращает его ID.
     * @return ID созданного профиля
     */
    private Long createProfileAndReturnId() {
        String msisdn = MsisdnGenerator.generateUniqueMsisdn();
        Response response = profileApi.createProfile(msisdn, 
                Config.DEFAULT_PRICE_PLAN_ID, Config.DEFAULT_USER_ID);
        ProfileResponse profile = extractProfileFromResponse(response);
        return profile.getId();
    }
}
