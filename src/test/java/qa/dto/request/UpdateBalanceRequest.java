package qa.dto.request;

/**
 * DTO для обновления суммы на балансе абонента.
 * 
 * Используется в API запросе:
 * PUT /api/balance/update/{id}
 * Body: {"amount": 1500.00}
 * 
 * Паттерн Builder применяется для единообразия с другими request DTO,
 * хотя здесь всего одно поле.
 */
public class UpdateBalanceRequest {
    
    /**
     * Новая сумма баланса.
     * 
     * ВАЖНО: Это не добавление/вычитание, а установка конкретного значения!
     * Если баланс был 1000 и вы отправите amount=500, баланс станет 500, а не 1500.
     * 
     * Тип Double используется для денежных сумм с копейками: 1234.56
     */
    private final Double amount;

    /**
     * Приватный конструктор, объекты создаются только через Builder.
     */
    private UpdateBalanceRequest(Builder builder) {
        this.amount = builder.amount;
    }

    /**
     * Создать новый Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Получить сумму баланса.
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Builder для создания UpdateBalanceRequest.
     * 
     * Использование:
     * UpdateBalanceRequest request = UpdateBalanceRequest.builder()
     *     .amount(1500.0)
     *     .build();
     */
    public static class Builder {
        private Double amount;

        /**
         * Установить новую сумму баланса.
         * @param amount сумма (может быть дробной: 123.45)
         * @return this для цепочки вызовов
         */
        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Создать финальный immutable объект.
         */
        public UpdateBalanceRequest build() {
            return new UpdateBalanceRequest(this);
        }
    }
}
