package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Library;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("LibraryRepository")
public interface LibraryRepository extends GenericImportRepository<Library> {
    List<Library> findByStatusOrderByIdentifier(Boolean status);

    Library findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

}
