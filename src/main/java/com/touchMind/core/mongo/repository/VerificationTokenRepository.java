package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.model.VerificationToken;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("VerificationTokenRepository")
public interface VerificationTokenRepository extends GenericImportRepository<VerificationToken> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
