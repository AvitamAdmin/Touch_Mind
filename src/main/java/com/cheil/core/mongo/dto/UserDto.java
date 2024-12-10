package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDto extends CommonDto {
    private Set<String> roles;
    private String referredBy;
    private String username;
    private String password;
    private String organization;
    private String otp;
    private String node;
    private String passwordConfirm;
    private Locale locale;
    private Set<String> subsidiaries;
    private String resetPasswordToken;
    private String level;
}
