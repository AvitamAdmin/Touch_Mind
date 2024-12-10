package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Config;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("ConfigRepository")
public interface ConfigRepository extends GenericImportRepository<Config> {
}
