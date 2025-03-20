package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository("UserRepository")
public interface UserRepository extends GenericImportRepository<User> {
    User findByUsername(String username);

    List<User> findByLocale(Locale locale);

    User findByResetPasswordToken(String token);

    User findByIdentifier(String id);

    void deleteByIdentifier(String valueOf);

    void deleteById(String id);
}
