package qa.config;

/**
 * Центральное хранилище всех конфигурационных констант для тестового фреймворка.
 * 
 * Этот класс содержит:
 * - URL адреса API эндпоинтов
 * - Данные для авторизации администратора
 * - Значения по умолчанию для создания тестовых данных
 * 
 * Все константы объявлены как public static final, что означает:
 * - public: доступны из любого места в проекте
 * - static: принадлежат классу, а не экземпляру (можно использовать без создания объекта)
 * - final: значения не могут быть изменены после инициализации
 */
public class Config {
    
    // ==================== БАЗОВЫЕ НАСТРОЙКИ ====================
    
    /**
     * Базовый URL тестируемого API.
     * Используется как префикс для всех HTTP запросов.
     * Например: BASE_URL + AUTH_LOGIN = "http://195.38.164.168:7173/api/auth/sign_in"
     */
    public static final String BASE_URL = "http://195.38.164.168:7173";
    
    // ==================== УЧЕТНЫЕ ДАННЫЕ АДМИНИСТРАТОРА ====================
    
    /**
     * Имя пользователя администратора для авторизации.
     * Используется для получения JWT токена, который затем применяется во всех тестах.
     */
    public static final String ADMIN_USERNAME = "superuser";
    
    /**
     * Пароль администратора для авторизации.
     * Используется вместе с ADMIN_USERNAME для получения JWT токена.
     */
    public static final String ADMIN_PASSWORD = "Admin123!@#";
    
    // ==================== ЭНДПОИНТЫ АВТОРИЗАЦИИ ====================
    
    /**
     * Эндпоинт для входа в систему (логин).
     * Принимает username и password, возвращает JWT токен.
     */
    public static final String AUTH_LOGIN = "/api/auth/sign_in";
    
    /**
     * Эндпоинт для регистрации нового пользователя.
     * Используется в тестах авторизации для проверки создания новых аккаунтов.
     */
    public static final String AUTH_SIGNUP = "/api/auth/sign_up";

    // ==================== ЭНДПОИНТЫ ПРОФИЛЕЙ (ADMIN) ====================
    
    /**
     * Получить список всех профилей абонентов.
     * Возвращает массив всех профилей в системе.
     */
    public static final String PROFILE_ALL = "/api/admin/profile/all";
    
    /**
     * Получить список всех удаленных профилей.
     * Возвращает профили со статусом "удален" (soft delete).
     */
    public static final String PROFILE_ALL_REMOVED = "/api/admin/profile/all-removed";
    
    /**
     * Создать новый профиль абонента.
     * Принимает JSON с msisdn, pricePlanId, userId.
     */
    public static final String PROFILE_CREATE = "/api/admin/profile/create";
    
    /**
     * Получить профиль по ID.
     * Использование: PROFILE_BY_ID + "{id}" -> "/api/admin/profile/123"
     */
    public static final String PROFILE_BY_ID = "/api/admin/profile/";
    
    /**
     * Получить профиль по номеру телефона (MSISDN).
     * Использование: PROFILE_BY_MSISDN + "{msisdn}" -> "/api/admin/profile/getByMsisdn/996801234567"
     */
    public static final String PROFILE_BY_MSISDN = "/api/admin/profile/getByMsisdn/";
    
    /**
     * Обновить существующий профиль.
     * Использование: PROFILE_UPDATE + "{id}" -> "/api/admin/profile/update/123"
     */
    public static final String PROFILE_UPDATE = "/api/admin/profile/update/";
    
    /**
     * Удалить профиль (soft delete - помечает как удаленный).
     * Использование: PROFILE_DELETE + "{id}" -> "/api/admin/profile/delete/123"
     */
    public static final String PROFILE_DELETE = "/api/admin/profile/delete/";

    // ==================== ЭНДПОИНТЫ БАЛАНСОВ (ADMIN) ====================
    
    /**
     * Получить список всех балансов абонентов.
     * Баланс создается автоматически при создании профиля.
     */
    public static final String BALANCE_ALL = "/api/balance/all";
    
    /**
     * Получить баланс по ID профиля.
     * Использование: BALANCE_BY_ID + "{profileId}" -> "/api/balance/123"
     */
    public static final String BALANCE_BY_ID = "/api/balance/";
    
    /**
     * Обновить сумму на балансе абонента.
     * Использование: BALANCE_UPDATE + "{profileId}" -> "/api/balance/update/123"
     * Принимает JSON с полем "amount" (новая сумма баланса).
     */
    public static final String BALANCE_UPDATE = "/api/balance/update/";

    // ==================== ЭНДПОИНТЫ СЧЕТЧИКОВ (ADMIN) ====================
    
    /**
     * Получить список всех счетчиков (counters) абонентов.
     * Counter хранит остатки минут, SMS, мегабайт по тарифному плану.
     */
    public static final String COUNTER_ALL = "/api/admin/counter/all";
    
    /**
     * Получить список активных счетчиков.
     * Активный counter = у профиля есть не израсходованные ресурсы.
     */
    public static final String COUNTER_ALL_ACTIVE = "/api/admin/counter/all-active";
    
    /**
     * Получить счетчик по ID профиля.
     * Использование: COUNTER_BY_ID + "{profileId}" -> "/api/admin/counter/123"
     */
    public static final String COUNTER_BY_ID = "/api/admin/counter/";

    // ==================== ЗНАЧЕНИЯ ПО УМОЛЧАНИЮ ====================
    
    /**
     * ID тарифного плана по умолчанию.
     * Используется при создании тестовых профилей.
     * Значение 2L означает: тип Long, значение 2.
     */
    public static final Long DEFAULT_PRICE_PLAN_ID = 2L;
    
    /**
     * ID пользователя по умолчанию.
     * Используется при создании тестовых профилей.
     * Связывает профиль с конкретным пользователем в системе.
     */
    public static final Long DEFAULT_USER_ID = 1L;
    
    /**
     * Префикс номера телефона для генерации уникальных MSISDN.
     * Формат полного номера: 99680 + 7 случайных цифр = "996801234567" (12 символов).
     * 99680 - код Кыргызстана + код оператора.
     */
    public static final String PHONE_PREFIX = "99680";
}

