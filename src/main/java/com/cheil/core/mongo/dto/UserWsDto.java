package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserWsDto extends CommonWsDto {
    private List<UserDto> users;
    private List<RoleDto> userRoles;
    private Locale[] locales;
    private boolean isAdmin;
}
