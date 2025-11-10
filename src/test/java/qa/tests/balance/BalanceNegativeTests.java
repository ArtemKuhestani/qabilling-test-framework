package qa.tests.balance;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.tests.BaseTest;

/**
 * Негативные тесты для операций с балансом.
 * Проверяют обработку ошибок и невалидных данных.
 */
public class BalanceNegativeTests extends BaseTest {

    /**
     * Тест: получение баланса по несуществующему ID.
     * GET /api/balance/999999
     * 
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenBalanceNotFoundById() {
        Long nonExistentId = 999999L;
        
        Response response = balanceApi.getBalanceById(nonExistentId);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: обновление баланса отрицательным значением.
     * PUT /api/balance с amount = -100.0
     * 
     * Проверяет валидацию: баланс не может быть отрицательным.
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenUpdatingWithNegativeAmount() {
        Long profileId = 1L;
        Double negativeAmount = -100.0;
        
        Response response = balanceApi.updateBalance(profileId, negativeAmount);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: обновление баланса со значением null.
     * 
     * Проверяет обязательность поля amount.
     * Ожидаемый результат: 400 BAD_REQUEST
     */
    @Test
    public void shouldReturn400WhenUpdatingWithNullAmount() {
        Long profileId = 1L;
        
        Response response = balanceApi.updateBalance(profileId, (Double) null);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: обновление баланса для несуществующего профиля.
     * 
     * Ожидаемый результат: 400 BAD_REQUEST
     * (API не создаёт баланс автоматически)
     */
    @Test
    public void shouldReturn400WhenUpdatingNonExistentBalance() {
        Long nonExistentId = 999999L;
        Double amount = 100.0;
        
        Response response = balanceApi.updateBalance(nonExistentId, amount);
        
        assertResponseIsBadRequest(response);
    }

    /**
     * Тест: получение баланса с невалидным ID = 0.
     * 
     * Проверяет граничное значение (ID должен быть > 0).
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenRequestingBalanceWithInvalidId() {
        Response response = balanceApi.getBalanceById(0L);
        
        assertResponseIsNotFound(response);
    }
}
