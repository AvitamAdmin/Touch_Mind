package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Country;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CountryRepository")
public interface CountryRepository extends GenericImportRepository<Country> {

    List<Country> findByStatusOrderByIdentifier(Boolean status);
}
