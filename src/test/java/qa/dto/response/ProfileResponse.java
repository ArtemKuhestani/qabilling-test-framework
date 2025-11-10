package qa.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO для ответа API с данными профиля абонента.
 * 
 * Jackson автоматически преобразует JSON ответ в этот Java объект (десериализация).
 * 
 * @JsonIgnoreProperties(ignoreUnknown = true) означает:
 * "Игнорировать поля из JSON, которых нет в этом классе".
 * Это защита от изменений API - если сервер вернет новое поле, тесты не упадут.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileResponse {
    
    /** Уникальный идентификатор профиля */
    private Long id;
    
    /** Номер телефона в формате 996XXXXXXXXX */
    private String msisdn;
    
    /** ID тарифного плана профиля */
    private Long pricePlanId;
    
    /** ID пользователя-владельца */
    private Long userId;
    
    /** Флаг удаления: true = профиль удален (soft delete) */
    private Boolean removed;
    
    /** Статус профиля (например: "ACTIVE", "BLOCKED") */
    private String status;
    
    /** Доступность профиля (недокументированное поле в API) */
    private Boolean available;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public Long getPricePlanId() { return pricePlanId; }
    public void setPricePlanId(Long pricePlanId) { this.pricePlanId = pricePlanId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Boolean getRemoved() { return removed; }
    public void setRemoved(Boolean removed) { this.removed = removed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
