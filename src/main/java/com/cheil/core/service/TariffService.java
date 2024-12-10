package com.cheil.core.service;

import com.cheil.core.mongo.model.Tariff;
import com.cheil.core.mongo.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TariffService {

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private Environment env;

    //TODO check if this fetch the record correctly
    public void saveTariff(Tariff tariff) {
        tariffRepository.save(tariff);
    }

    //TODO check if this fetch the record correctly
    public void saveAllTariffs(List<Tariff> tariffs) {
        tariffRepository.saveAll(tariffs);
    }

    public Tariff findBySessionIdAndDeviceIdAndPlanId(String sessionId, String deviceId, String planId) {
        Optional<Tariff> tariff = tariffRepository.findBySessionIdAndDeviceIdAndPlanId(sessionId, deviceId, planId).stream().findFirst();
        return tariff.orElse(null);
    }

    public List<Tariff> findBySessionIdAndPlanId(String sessiomId, String planId) {
        return new ArrayList<>(tariffRepository.findAllBySessionIdAndPlanId(sessiomId, planId));
    }
}
