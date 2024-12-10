package com.touchmind.form;

import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.model.Subsidiary;
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
public class UserForm extends BaseForm {
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    private String node;
    private Set<Role> roles;
    private Set<Subsidiary> subsidiaries;
    private Locale locale;
    private String usernameMasked;
    private String emailMasked;
}
