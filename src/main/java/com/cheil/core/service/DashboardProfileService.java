package com.cheil.core.service;

import com.cheil.core.mongo.dto.DashboardProfileWsDto;
import com.cheil.core.mongo.model.DashboardProfile;
import com.cheil.form.DashboardProfileForm;

import java.util.List;
import java.util.Set;

public interface DashboardProfileService {
    List<DashboardProfile> getDashboardProfiles();

    DashboardProfileForm editDashboardProfiles(String id);

    DashboardProfileForm addDashboardProfile(DashboardProfileForm dashboardProfileForm);

    void deleteDashboardProfile(String id);

    Set<String> getDashboardLabels();

    DashboardProfile getDashboardProfileByRecordId(String dashboardProfile);

    DashboardProfileWsDto handleEdit(DashboardProfileWsDto request);
}
