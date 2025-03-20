package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Country;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CountryRepository")
public interface CountryRepository extends GenericImportRepository<Country> {

    List<Country> findByStatusOrderByIdentifier(Boolean status);
}
