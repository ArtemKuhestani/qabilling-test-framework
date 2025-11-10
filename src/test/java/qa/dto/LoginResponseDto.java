package qa.dto;

/**
 * DTO для контента ответа при успешном логине.
 * Вложен в ApiResponseDto как поле "content".
 */
public class LoginResponseDto {
    
    /** Имя пользователя */
    private String username;
    
    /** Роль пользователя: "USER", "ADMIN", "MANAGER" */
    private String role;
    
    /** JWT токен для авторизации последующих запросов */
    private String token;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
