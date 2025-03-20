package com.touchMind.core.service;

public interface RecordIdCollectionService {
    String getDuplicateQaRecords(String entity);

    void repairDuplicateRecords();
}
