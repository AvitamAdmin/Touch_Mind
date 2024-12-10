package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Tariff;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("TariffRepository")
public interface TariffRepository extends GenericImportRepository<Tariff> {

    @Query("FROM Tariff WHERE sessionId = ?1 AND isActive=true")
    List<Tariff> findBySessionId(String sessionId);

    @Query("FROM Tariff WHERE sessionId = ?1 AND deviceId = ?2 AND planId = ?3")
    List<Tariff> findBySessionIdAndDeviceIdAndPlanId(String sessionId, String deviceId, String planId);

    @Query("FROM Tariff WHERE sessionId = ?1 AND planId = ?2  AND isActive=true")
    List<Tariff> findAllBySessionIdAndPlanId(String sessiomId, String planId);

    @Query("FROM Tariff WHERE sessionId = ?1 AND updatedDate >= ?2  AND isActive=true")
    List<Tariff> findAllByUpdatedDateAndSessionId(String sessiomId, Date date);

}
