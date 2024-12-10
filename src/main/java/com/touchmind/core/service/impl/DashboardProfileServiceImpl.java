package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.DashboardProfileDto;
import com.touchmind.core.mongo.dto.DashboardProfileWsDto;
import com.touchmind.core.mongo.model.DashboardProfile;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.DashboardProfileRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.DashboardProfileService;
import com.touchmind.core.service.LocatorService;
import com.touchmind.form.DashboardProfileForm;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        DashboardProfile dashboardProfileOptional = dashboardProfileRepository.findByRecordId(id);
        if (dashboardProfileOptional != null) {
            return modelMapper.map(dashboardProfileOptional, DashboardProfileForm.class);
        }
        return null;
    }

    @Override
    public DashboardProfileForm addDashboardProfile(DashboardProfileForm dashboardProfileForm) {
        DashboardProfile dashboardProfile = modelMapper.map(dashboardProfileForm, DashboardProfile.class);
        if (dashboardProfile.getRecordId() != null) {
            DashboardProfile testLocatorRecord = dashboardProfileRepository.findByRecordId(dashboardProfile.getRecordId());
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

        if (dashboardProfile.getRecordId() == null) {
            dashboardProfile.setRecordId(String.valueOf(dashboardProfile.getId().getTimestamp()));
            dashboardProfileRepository.save(dashboardProfile);
        }
        return dashboardProfileForm;
    }

    @Override
    public void deleteDashboardProfile(String id) {
        DashboardProfile testLocatorRecord = dashboardProfileRepository.findByRecordId(id);
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
    public DashboardProfile getDashboardProfileByRecordId(String dashboardProfile) {
        DashboardProfile dashboardProfileOptional = dashboardProfileRepository.findByRecordId(dashboardProfile);
        return dashboardProfileOptional;
    }

    @Override
    public DashboardProfileWsDto handleEdit(DashboardProfileWsDto request) {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        List<DashboardProfileDto> dashboardProfiles = request.getDashboardProfiles();
        List<DashboardProfile> dashboardProfileList = new ArrayList<>();
        DashboardProfile requestData = null;
        for (DashboardProfileDto dashboardProfile : dashboardProfiles) {
            if (dashboardProfile.getRecordId() != null) {
                requestData = dashboardProfileRepository.findByRecordId(dashboardProfile.getRecordId());
                modelMapper.map(dashboardProfile, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.DASHBOARD_PROFILE, dashboardProfile.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(dashboardProfile, DashboardProfile.class);
            }
            baseService.populateCommonData(requestData);
            dashboardProfileRepository.save(requestData);
            if (dashboardProfile.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
            dashboardProfileRepository.save(requestData);
            dashboardProfileList.add(requestData);
        }
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfileList, List.class));
        return dashboardProfileWsDto;

    }
}
