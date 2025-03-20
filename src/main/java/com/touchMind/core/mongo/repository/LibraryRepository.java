package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Library;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("LibraryRepository")
public interface LibraryRepository extends GenericImportRepository<Library> {
    List<Library> findByStatusOrderByIdentifier(Boolean status);

    Library findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}
