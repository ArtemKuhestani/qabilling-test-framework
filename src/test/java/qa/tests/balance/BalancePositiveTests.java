package qa.tests.balance;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.config.Config;
import qa.dto.response.BalanceResponse;
import qa.dto.response.ProfileResponse;
import qa.tests.BaseTest;
import qa.utils.MsisdnGenerator;

import static org.testng.Assert.*;

/**
 * Позитивные тесты для операций с балансом.
 * Проверяют успешные сценарии работы с Balance API.
 */
public class BalancePositiveTests extends BaseTest {

    /**
     * Тест: получение списка всех балансов.
     * GET /api/balance
     * 
     * Ожидаемый результат: 200 OK, в ответе массив content
     */
    @Test
    public void shouldReturnAllBalances() {
        Response response = balanceApi.getAllBalances();
        
        assertResponseIsOk(response);
        assertNotNull(response.jsonPath().getList("content"));
    }

    /**
     * Тест: получение баланса по существующему profileId.
     * 
     * Предусловие: создаём новый профиль
     * GET /api/balance/{profileId}
     * 
     * Проверки:
     * - 200 OK
     * - balance.id не null
     * - balance.profileId совпадает с созданным
     */
    @Test
    public void shouldReturnBalanceByExistingProfileId() {
        Long profileId = createProfileAndReturnId();
        
        Response response = balanceApi.getBalanceById(profileId);
        
        assertResponseIsOk(response);
        BalanceResponse balance = extractBalanceFromResponse(response);
        assertNotNull(balance.getId());
        assertEquals(balance.getProfileId(), profileId);
    }

    /**
     * Тест: обновление баланса на новое значение.
     * 
     * ВАЖНО: API заменяет баланс, а не добавляет!
     * 
     * Сценарий:
     * 1. Создаём профиль
     * 2. Обновляем баланс на 500.75
     * 3. Получаем баланс и проверяем значение
     */
    @Test
    public void shouldUpdateBalanceAmount() {
        Long profileId = createProfileAndReturnId();
        Double newAmount = 500.75;
        
        Response updateResponse = balanceApi.updateBalance(profileId, newAmount);
        assertResponseIsOk(updateResponse);
        
        Response getResponse = balanceApi.getBalanceById(profileId);
        assertResponseIsOk(getResponse);
        
        BalanceResponse balance = extractBalanceFromResponse(getResponse);
        assertBalanceAmount(balance, newAmount);
    }

    /**
     * Тест: обновление баланса на 0.
     * 
     * Проверяет, что можно обнулить баланс.
     * Это граничное значение (минимально допустимое).
     */
    @Test
    public void shouldUpdateBalanceToZero() {
        Long profileId = createProfileAndReturnId();
        Double newAmount = 0.0;
        
        Response updateResponse = balanceApi.updateBalance(profileId, newAmount);
        assertResponseIsOk(updateResponse);
        
        Response getResponse = balanceApi.getBalanceById(profileId);
        BalanceResponse balance = extractBalanceFromResponse(getResponse);
        
        assertBalanceAmount(balance, newAmount);
    }

    /**
     * Тест: множественное обновление баланса.
     * 
     * Проверяет, что последнее значение побеждает.
     * Обновляем 3 раза: 100.0 -> 250.50 -> 999.99
     * 
     * Ожидаемый результат: финальный баланс = 999.99
     */
    @Test
    public void shouldUpdateBalanceMultipleTimes() {
        Long profileId = createProfileAndReturnId();
        
        balanceApi.updateBalance(profileId, 100.0);
        balanceApi.updateBalance(profileId, 250.50);
        Response finalUpdate = balanceApi.updateBalance(profileId, 999.99);
        
        assertResponseIsOk(finalUpdate);
        
        Response getResponse = balanceApi.getBalanceById(profileId);
        BalanceResponse balance = extractBalanceFromResponse(getResponse);
        
        assertBalanceAmount(balance, 999.99);
    }

    /**
     * Вспомогательный метод для создания профиля и получения его ID.
     * 
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
