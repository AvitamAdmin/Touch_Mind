package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Config;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("ConfigRepository")
public interface ConfigRepository extends GenericImportRepository<Config> {
}
