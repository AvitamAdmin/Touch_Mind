package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Media;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MediaRepository")
public interface MediaRepository extends GenericImportRepository<Media> {
    Media findByIdentifier(String valueOf);

    void deleteByIdentifier(String valueOf);

    List<Media> findByStatusOrderByIdentifier(Boolean status);

}
