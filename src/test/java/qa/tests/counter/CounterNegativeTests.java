package qa.tests.counter;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import qa.tests.BaseTest;

/**
 * Негативные тесты для операций со счётчиками.
 * Проверяют обработку невалидных ID.
 */
public class CounterNegativeTests extends BaseTest {

    /**
     * Тест: получение счётчика по несуществующему profileId.
     * GET /api/counter/999999
     * 
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenCounterNotFoundById() {
        Long nonExistentId = 999999L;
        
        Response response = counterApi.getCounterById(nonExistentId);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: получение счётчика с невалидным ID = 0.
     * 
     * ID должен быть положительным числом.
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenRequestingCounterWithInvalidId() {
        Response response = counterApi.getCounterById(0L);
        
        assertResponseIsNotFound(response);
    }

    /**
     * Тест: получение счётчика с отрицательным ID.
     * 
     * Проверяет валидацию отрицательных значений.
     * Ожидаемый результат: 404 NOT_FOUND
     */
    @Test
    public void shouldReturn404WhenRequestingCounterWithNegativeId() {
        Response response = counterApi.getCounterById(-1L);
        
        assertResponseIsNotFound(response);
    }
}
