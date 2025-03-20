package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.DashboardProfileDto;
import com.touchMind.core.mongo.dto.DashboardProfileWsDto;
import com.touchMind.core.mongo.model.DashboardProfile;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.DashboardProfileRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.DashboardProfileService;
import com.touchMind.core.service.LocatorService;
import com.touchMind.form.DashboardProfileForm;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DashboardProfileServiceImpl implements DashboardProfileService {
    public static final String ADMIN_DASHBOARDPROFILE = "/admin/dashboardProfile";
    @Autowired
    private DashboardProfileRepository dashboardProfileRepository;
    @Autowired
    private LocatorService locatorService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public List<DashboardProfile> getDashboardProfiles() {
        return dashboardProfileRepository.findAll();
    }

    @Override
    public DashboardProfileForm editDashboardProfiles(String id) {
        DashboardProfile dashboardProfileOptional = dashboardProfileRepository.findByIdentifier(id);
        if (dashboardProfileOptional != null) {
            return modelMapper.map(dashboardProfileOptional, DashboardProfileForm.class);
        }
        return null;
    }

    @Override
    public DashboardProfileForm addDashboardProfile(DashboardProfileForm dashboardProfileForm) {
        DashboardProfile dashboardProfile = modelMapper.map(dashboardProfileForm, DashboardProfile.class);
        if (dashboardProfile.getIdentifier() != null) {
            DashboardProfile testLocatorRecord = dashboardProfileRepository.findByIdentifier(dashboardProfile.getIdentifier());
            if (testLocatorRecord != null) {
                DashboardProfile dataBaseRecord = testLocatorRecord;
                ObjectId id = dataBaseRecord.getId();
                modelMapper.map(dashboardProfile, dataBaseRecord);
                dataBaseRecord.setId(id);
                dashboardProfileRepository.save(dataBaseRecord);
            }
        } else {
            dashboardProfileRepository.save(dashboardProfile);
        }

        if (dashboardProfile.getIdentifier() == null) {
            dashboardProfile.setIdentifier(String.valueOf(dashboardProfile.getId().getTimestamp()));
            dashboardProfileRepository.save(dashboardProfile);
        }
        return dashboardProfileForm;
    }

    @Override
    public void deleteDashboardProfile(String id) {
        DashboardProfile testLocatorRecord = dashboardProfileRepository.findByIdentifier(id);
        if (testLocatorRecord != null) {
            dashboardProfileRepository.delete(testLocatorRecord);
        }
    }

    @Override
    public Set<String> getDashboardLabels() {
        List<TestLocator> testLocatorList = locatorService.getLocators();
        Set<String> labels = new HashSet<>();
        testLocatorList.forEach(locator -> {
            if (CollectionUtils.isNotEmpty(locator.getLabels())) {
                labels.addAll(locator.getLabels());
            }
        });
        return labels;
    }

    @Override
    public DashboardProfile getDashboardProfileByIdentifier(String dashboardProfile) {
        DashboardProfile dashboardProfileOptional = dashboardProfileRepository.findByIdentifier(dashboardProfile);
        return dashboardProfileOptional;
    }

    @Override
    public DashboardProfileWsDto handleEdit(DashboardProfileWsDto request) {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        List<DashboardProfileDto> dashboardProfiles = request.getDashboardProfiles();
        List<DashboardProfile> dashboardProfileList = new ArrayList<>();
        DashboardProfile requestData = null;
        for (DashboardProfileDto dashboardProfile : dashboardProfiles) {
            if (dashboardProfile.isAdd() && baseService.validateIdentifier(EntityConstants.DASHBOARD_PROFILE, dashboardProfile.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = dashboardProfileRepository.findByIdentifier(dashboardProfile.getIdentifier());
            if (requestData != null) {
                modelMapper.map(dashboardProfile, requestData);
            } else {
                requestData = modelMapper.map(dashboardProfile, DashboardProfile.class);
            }
            baseService.populateCommonData(requestData);
            dashboardProfileRepository.save(requestData);
            dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
            dashboardProfileList.add(requestData);
        }
        Type listType = new TypeToken<List<DashboardProfileDto>>() {
        }.getType();
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfileList, listType));
        dashboardProfileWsDto.setMessage("Dashboard profile updated successfully");
        return dashboardProfileWsDto;

    }
}
