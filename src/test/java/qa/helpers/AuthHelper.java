package qa.helpers;

import io.restassured.RestAssured;
import qa.config.Config;
import qa.dto.ApiResponseDto;
import qa.dto.LoginRequestDto;

/**
 * Помощник для авторизации в API тестируемой системы.
 * 
 * Этот класс решает важную задачу:
 * - Получение JWT токена для авторизации
 * - Кэширование токена (чтобы не логиниться перед каждым тестом)
 * - Переиспользование токена во всех тестах
 * 
 * JWT (JSON Web Token) - это зашифрованная строка, которая подтверждает
 * что пользователь успешно авторизован. Формат примерно такой:
 * "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY..."
 * 
 * Этот токен нужно добавлять в каждый HTTP запрос в заголовок:
 * Authorization: Bearer {token}
 * 
 * Паттерн использования:
 * 1. Первый тест вызывает getToken()
 * 2. AuthHelper логинится и сохраняет токен
 * 3. Все последующие тесты получают сохраненный токен без повторного логина
 * 4. Это ускоряет выполнение тестов
 */
public class AuthHelper {

    /**
     * Сохраненный JWT токен администратора.
     * 
     * static означает что переменная принадлежит классу, а не экземпляру.
     * Это важно для кэширования - один токен на все тесты.
     * 
     * Значение null означает что токен еще не получен.
     * После первого вызова getToken() здесь будет JWT строка.
     */
    private static String token;

    /**
     * Получить JWT токен для авторизации в API.
     * 
     * Логика работы:
     * 1. Если токен уже получен (token != null) - возвращаем его
     * 2. Если токена нет (token == null) - вызываем login() для получения
     * 3. После login() токен сохранится и будет возвращен
     * 
     * @return строка с JWT токеном администратора
     * 
     * Пример использования:
     * String token = AuthHelper.getToken();
     * RequestSpecification spec = new RequestSpecBuilder()
     *     .addHeader("Authorization", "Bearer " + token)
     *     .build();
     * 
     * Это публичный метод (public), поэтому доступен из любого места в проекте.
     * Это статический метод (static), вызывается как AuthHelper.getToken() без создания объекта.
     */
    public static String getToken() {
        if (token == null) {
            login();
        }
        return token;
    }

    /**
     * Выполнить вход в систему и получить JWT токен.
     * 
     * Последовательность действий:
     * 1. Создается объект запроса LoginRequestDto с логином и паролем из Config
     * 2. Отправляется POST запрос на эндпоинт /api/auth/sign_in
     * 3. Ответ автоматически преобразуется в объект ApiResponseDto
     * 4. Из ответа извлекается токен (response.getContent().getToken())
     * 5. Токен сохраняется в переменную token
     * 
     * Это приватный метод (private), вызывается только внутри этого класса.
     * Внешний код использует только getToken(), который сам решит когда вызвать login().
     * 
     * Формат ответа API:
     * {
     *   "code": "OK",
     *   "content": {
     *     "username": "superuser",
     *     "role": "ADMIN",
     *     "token": "eyJhbGciOiJIUzI1NiIs..."
     *   }
     * }
     */
    private static void login() {
        LoginRequestDto loginRequest = new LoginRequestDto(Config.ADMIN_USERNAME, Config.ADMIN_PASSWORD);

        ApiResponseDto response = RestAssured.given()
            .contentType("application/json")
            .baseUri(Config.BASE_URL)
            .body(loginRequest)
            .post(Config.AUTH_LOGIN)
            .as(ApiResponseDto.class);

        token = response.getContent().getToken();
    }

    /**
     * Сбросить сохраненный токен (очистить кэш).
     * 
     * После вызова этого метода следующий getToken() снова выполнит login().
     * 
     * Использование:
     * - Тестирование истечения токена
     * - Смена пользователя (если понадобится логин под другим аккаунтом)
     * - Очистка состояния между наборами тестов
     * 
     * На практике редко нужен, так как токены обычно живут достаточно долго
     * для прохождения всех тестов.
     */
    public static void resetToken() {
        token = null;
    }
}
