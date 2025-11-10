package qa.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO для ответа API с данными счетчика ресурсов абонента.
 * Счетчик хранит остатки минут, SMS, интернета по тарифу.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterResponse {
    
    /** ID счетчика (совпадает с profileId) */
    private Long id;
    
    /** Остаток интернет-трафика в мегабайтах */
    private Long megabytes;
    
    /** Остаток времени разговора в секундах (60 секунд = 1 минута) */
    private Long seconds;
    
    /** Остаток SMS сообщений */
    private Integer sms;
    
    /** ID профиля-владельца счетчика */
    private Long profileId;
    
    /** Статус счетчика (например: "ACTIVE") */
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMegabytes() { return megabytes; }
    public void setMegabytes(Long megabytes) { this.megabytes = megabytes; }

    public Long getSeconds() { return seconds; }
    public void setSeconds(Long seconds) { this.seconds = seconds; }

    public Integer getSms() { return sms; }
    public void setSms(Integer sms) { this.sms = sms; }

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
