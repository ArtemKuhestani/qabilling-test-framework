package qa.dto;

/**
 * DTO для запроса на регистрацию нового пользователя.
 * 
 * Отправляется в POST /api/auth/sign_up
 * Используется в тестах авторизации.
 */
public class SignUpRequestDto {
    
    /** Уникальное имя пользователя */
    private String username;
    
    /** Пароль */
    private String password;
    
    /** Имя */
    private String firstName;
    
    /** Фамилия */
    private String lastName;
    
    /** ID Telegram чата для уведомлений */
    private String telegramChatId;

    public SignUpRequestDto(String username, String password, String firstName, String lastName, String telegramChatId) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telegramChatId = telegramChatId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
}
