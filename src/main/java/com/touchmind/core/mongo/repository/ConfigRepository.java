package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Config;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("ConfigRepository")
public interface ConfigRepository extends GenericImportRepository<Config> {
}
