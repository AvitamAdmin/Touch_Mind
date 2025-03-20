package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.DashboardProfileWsDto;
import com.touchMind.core.mongo.model.DashboardProfile;
import com.touchMind.form.DashboardProfileForm;

import java.util.List;
import java.util.Set;

public interface DashboardProfileService {
    List<DashboardProfile> getDashboardProfiles();

    DashboardProfileForm editDashboardProfiles(String id);

    DashboardProfileForm addDashboardProfile(DashboardProfileForm dashboardProfileForm);

    void deleteDashboardProfile(String id);

    Set<String> getDashboardLabels();

    DashboardProfile getDashboardProfileByIdentifier(String dashboardProfile);

    DashboardProfileWsDto handleEdit(DashboardProfileWsDto request);
}
