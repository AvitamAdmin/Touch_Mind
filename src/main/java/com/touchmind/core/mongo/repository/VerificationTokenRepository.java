package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.model.VerificationToken;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("VerificationTokenRepository")
public interface VerificationTokenRepository extends GenericImportRepository<VerificationToken> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
