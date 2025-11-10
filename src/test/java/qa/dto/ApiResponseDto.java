package qa.dto;

/**
 * DTO для стандартного ответа API при авторизации.
 * 
 * Структура JSON ответа:
 * {
 *   "code": "OK",
 *   "content": {
 *     "username": "superuser",
 *     "role": "ADMIN",
 *     "token": "eyJhbGc..."
 *   }
 * }
 */
public class ApiResponseDto {
    
    /** Код ответа: "OK" при успехе, "ERROR" при ошибке */
    private String code;
    
    /** Контент ответа с данными пользователя и токеном */
    private LoginResponseDto content;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LoginResponseDto getContent() { return content; }
    public void setContent(LoginResponseDto content) { this.content = content; }
}
