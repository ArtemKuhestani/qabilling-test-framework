package qa.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO для ответа API с данными баланса абонента.
 * Jackson преобразует JSON в этот объект.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceResponse {
    
    /** ID баланса (совпадает с profileId) */
    private Long id;
    
    /** Текущая сумма на балансе */
    private Double amount;
    
    /** ID профиля-владельца баланса */
    private Long profileId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
}
