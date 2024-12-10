package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Media;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MediaRepository")
public interface MediaRepository extends GenericImportRepository<Media> {
    Media findByRecordId(String valueOf);

    void deleteByRecordId(String valueOf);

    List<Media> findByStatusOrderByIdentifier(Boolean status);

}
