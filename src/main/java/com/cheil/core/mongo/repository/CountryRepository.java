package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Country;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CountryRepository")
public interface CountryRepository extends GenericImportRepository<Country> {

    List<Country> findByStatusOrderByIdentifier(Boolean status);
}
