package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.DashboardProfileWsDto;
import com.touchmind.core.mongo.model.DashboardProfile;
import com.touchmind.form.DashboardProfileForm;

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
