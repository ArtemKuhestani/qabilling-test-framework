package qa.tests.auth;

import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.testng.annotations.Test;
import qa.config.Config;
import qa.dto.LoginRequestDto;
import qa.dto.SignUpRequestDto;
import qa.tests.BaseTest;

import static org.hamcrest.Matchers.equalTo;

/**
 * Тесты авторизации и регистрации пользователей.
 * Проверяют работу эндпоинтов sign-up и sign-in.
 */
public class AuthTests extends BaseTest {

    /** Faker для генерации уникальных username */
    private static final Faker faker = new Faker();

    /**
     * Генерирует уникальное имя пользователя.
     * @return случайный username (например, "john_doe123")
     */
    private String generateUniqueUsername() {
        return faker.internet().username();
    }

    /**
     * Позитивный тест: регистрация нового пользователя и успешный вход.
     * 
     * Сценарий:
     * 1. Создаём нового пользователя через sign-up
     * 2. Выполняем вход с его данными через sign-in
     * 
     * Ожидаемый результат: оба запроса возвращают 200 OK
     */
    @Test
    public void successfulSignUpAndSignIn() {
        String username = generateUniqueUsername();
        String password = "password123";
        SignUpRequestDto signUpRequest = new SignUpRequestDto(
                username, password, "Test", "User", "-1"
        );

        // Регистрация нового пользователя
        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(signUpRequest)
                .when()
                .post(Config.AUTH_SIGNUP)
                .then()
                .statusCode(200)
                .body("code", equalTo("OK"));

        // Авторизация созданного пользователя
        LoginRequestDto loginRequest = new LoginRequestDto(username, password);

        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post(Config.AUTH_LOGIN)
                .then()
                .statusCode(200)
                .body("code", equalTo("OK"));
    }

    /**
     * Негативный тест: повторная регистрация с существующим username.
     * 
     * Сценарий:
     * 1. Регистрируем пользователя
     * 2. Пытаемся зарегистрировать еще раз с тем же username
     * 
     * Ожидаемый результат: второй запрос возвращает 400 BAD_REQUEST
     */
    @Test
    public void signUpWithExistingUsernameShouldFail() {
        String username = generateUniqueUsername();
        SignUpRequestDto signUpRequest = new SignUpRequestDto(
                username, "password123", "Test", "User", "-1"
        );

        // Первая регистрация - успешная
        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(signUpRequest)
                .when()
                .post(Config.AUTH_SIGNUP)
                .then()
                .statusCode(200);

        // Повторная регистрация - ошибка
        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(signUpRequest)
                .when()
                .post(Config.AUTH_SIGNUP)
                .then()
                .statusCode(400)
                .body("status", equalTo("BAD_REQUEST"));
    }

    /**
     * Негативный тест: вход с неверным паролем.
     * 
     * Сценарий:
     * 1. Регистрируем пользователя с паролем "password123"
     * 2. Пытаемся войти с неверным паролем "wrong_password"
     * 
     * Ожидаемый результат: 401 UNAUTHORIZED
     */
    @Test
    public void signInWithWrongPasswordShouldFail() {
        String username = generateUniqueUsername();
        String password = "password123";
        SignUpRequestDto signUpRequest = new SignUpRequestDto(
                username, password, "Test", "User", "-1"
        );

        // Регистрация пользователя
        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(signUpRequest)
                .post(Config.AUTH_SIGNUP)
                .then()
                .statusCode(200);

        // Попытка входа с неверным паролем
        LoginRequestDto loginRequest = new LoginRequestDto(username, "wrong_password");

        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post(Config.AUTH_LOGIN)
                .then()
                .statusCode(401);
    }

    /**
     * Негативный тест: вход с несуществующим пользователем.
     * 
     * Сценарий:
     * Пытаемся войти с username, который не был зарегистрирован
     * 
     * Ожидаемый результат: 401 UNAUTHORIZED
     */
    @Test
    public void signInWithNonExistentUserShouldFail() {
        String username = generateUniqueUsername();
        LoginRequestDto loginRequest = new LoginRequestDto(username, "any_password");

        RestAssured.given()
                .baseUri(Config.BASE_URL)
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post(Config.AUTH_LOGIN)
                .then()
                .statusCode(401);
    }
}
