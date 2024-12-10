package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.User;
import com.cheil.core.mongo.model.VerificationToken;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("VerificationTokenRepository")
public interface VerificationTokenRepository extends GenericImportRepository<VerificationToken> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
