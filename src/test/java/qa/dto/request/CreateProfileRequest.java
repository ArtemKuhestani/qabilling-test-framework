package qa.dto.request;

/**
 * DTO (Data Transfer Object) для создания и обновления профиля абонента.
 * 
 * Используется в API запросах:
 * - POST /api/admin/profile/create (создание профиля)
 * - PUT /api/admin/profile/update/{id} (обновление профиля)
 * 
 * Реализует паттерн Builder для удобного создания объектов:
 * CreateProfileRequest request = CreateProfileRequest.builder()
 *     .msisdn("996801234567")
 *     .pricePlanId(2L)
 *     .userId(1L)
 *     .build();
 * 
 * Все поля final и устанавливаются через конструктор,
 * что делает объект immutable (неизменяемым) после создания.
 */
public class CreateProfileRequest {
    
    /**
     * Номер телефона абонента в международном формате (MSISDN).
     * Формат: 996XXXXXXXXX (12 цифр для Кыргызстана).
     * Должен быть уникальным в системе.
     */
    private final String msisdn;
    
    /**
     * ID тарифного плана который будет применен к профилю.
     * Определяет сколько минут/SMS/интернета получит абонент.
     * Должен существовать в таблице price_plans.
     */
    private final Long pricePlanId;
    
    /**
     * ID пользователя-владельца профиля.
     * Связывает профиль с конкретным пользователем (user) в системе.
     * Один пользователь может иметь несколько профилей.
     */
    private final Long userId;

    /**
     * Приватный конструктор, используется только внутри Builder.
     * Это гарантирует что объекты создаются только через builder().
     */
    private CreateProfileRequest(Builder builder) {
        this.msisdn = builder.msisdn;
        this.pricePlanId = builder.pricePlanId;
        this.userId = builder.userId;
    }

    /**
     * Создать новый Builder для пошагового конструирования объекта.
     * 
     * @return новый экземпляр Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Получить номер телефона */
    public String getMsisdn() {
        return msisdn;
    }

    /** Получить ID тарифного плана */
    public Long getPricePlanId() {
        return pricePlanId;
    }

    /** Получить ID пользователя-владельца */
    public Long getUserId() {
        return userId;
    }

    /**
     * Внутренний класс Builder для создания CreateProfileRequest.
     * 
     * Паттерн Builder позволяет:
     * - Создавать объекты пошагово: .msisdn(...).pricePlanId(...).userId(...)
     * - Делать код читаемым: сразу видно какое значение к какому полю относится
     * - Легко добавлять новые поля без изменения конструкторов
     */
    public static class Builder {
        private String msisdn;
        private Long pricePlanId;
        private Long userId;

        /**
         * Установить номер телефона.
         * @return this для цепочки вызовов (fluent interface)
         */
        public Builder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        /**
         * Установить ID тарифного плана.
         * @return this для цепочки вызовов
         */
        public Builder pricePlanId(Long pricePlanId) {
            this.pricePlanId = pricePlanId;
            return this;
        }

        /**
         * Установить ID пользователя.
         * @return this для цепочки вызовов
         */
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Создать финальный immutable объект CreateProfileRequest.
         * @return готовый объект запроса
         */
        public CreateProfileRequest build() {
            return new CreateProfileRequest(this);
        }
    }
}
