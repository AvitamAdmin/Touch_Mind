package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;
import java.util.Set;

@Document("user")
@Getter
@Setter
public class User extends CommonFields {
    private String referredBy;
    private String username;
    private String password;
    private String organization;
    private String otp;
    private String node;
    private String passwordConfirm;
    private Locale locale;
    @DBRef(lazy = true)
    private Set<Role> roles;
    private String resetPasswordToken;
}
