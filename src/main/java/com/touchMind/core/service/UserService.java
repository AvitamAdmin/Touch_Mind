package com.touchMind.core.service;


import com.touchMind.core.mongo.dto.UserWsDto;
import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.model.VerificationToken;

public interface UserService {
    void save(UserWsDto userWsDto);

    User findByUsername(String username);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    void saveRegisteredUser(User user);

    String validateVerificationToken(String token);

    User getUser(String verificationToken);

    boolean isAdminRole();

    User getCurrentUser();

    boolean updateResetPasswordToken(String token, String email);

    User getByResetPasswordToken(String token);

    void updatePassword(User user, String newPassword);

    boolean updateOtp(String token, String email);

    void populateCommonFields(UserWsDto userWsDto);
}
