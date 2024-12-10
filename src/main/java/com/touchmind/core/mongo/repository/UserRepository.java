package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository("UserRepository")
public interface UserRepository extends GenericImportRepository<User> {
    User findByUsername(String username);

    List<User> findByLocale(Locale locale);

    User findByResetPasswordToken(String token);

    User findByRecordId(String id);

    void deleteByRecordId(String valueOf);

    void deleteById(String id);
}
