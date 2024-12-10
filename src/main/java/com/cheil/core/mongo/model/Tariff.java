package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("Tariff")
@Getter
@Setter
@NoArgsConstructor
public class Tariff extends CommonFields {
    private String sessionId;
    private String planId;
    private String deviceId;
    private String tariffName;
    private Double otp;
    private Date updatedDate;
    private boolean isActive;

    @Override
    public String toString() {
        return "Tariff{" +
                ", sessionId='" + sessionId + '\'' +
                ", planId='" + planId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", tariffName='" + tariffName + '\'' +
                ", otp='" + otp + '\'' +
                ", updatedDate=" + updatedDate +
                '}';
    }

}
