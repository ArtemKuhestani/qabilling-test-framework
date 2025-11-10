package qa.dto;

/**
 * DTO для запроса на авторизацию (логин).
 * 
 * Отправляется в POST /api/auth/sign_in
 * Body: {"username": "...", "password": "..."}
 */
public class LoginRequestDto {
    
    /** Имя пользователя */
    private String username;
    
    /** Пароль пользователя */
    private String password;

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
