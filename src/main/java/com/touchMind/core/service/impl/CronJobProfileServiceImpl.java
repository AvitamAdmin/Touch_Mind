package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CronJobProfileDto;
import com.touchMind.core.mongo.dto.CronJobProfileWsDto;
import com.touchMind.core.mongo.model.CronJobProfile;
import com.touchMind.core.mongo.repository.CronJobProfileRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.CronJobProfileService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CronJobProfileServiceImpl implements CronJobProfileService {

    public static final String ADMIN_CRONJOBPROFILE = "/admin/cronjobProfile";

    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private BaseService baseService;

    @Override
    public CronJobProfileWsDto handleEdit(CronJobProfileWsDto request) {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        CronJobProfile requestData = null;
        List<CronJobProfileDto> cronJobProfiles = request.getCronJobProfiles();
        List<CronJobProfile> cronJobProfileList = new ArrayList<>();
        for (CronJobProfileDto cronJobProfile : cronJobProfiles) {
            if (cronJobProfile.isAdd() && baseService.validateIdentifier(EntityConstants.CRONJOB_PROFILE, cronJobProfile.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = cronJobProfileRepository.findByIdentifier(cronJobProfile.getIdentifier());
            if (requestData != null) {
                modelMapper.map(cronJobProfile, requestData);

            } else {
                requestData = modelMapper.map(cronJobProfile, CronJobProfile.class);
            }
            baseService.populateCommonData(requestData);
            cronJobProfileRepository.save(requestData);
            cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
            cronJobProfileList.add(requestData);
        }
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileList, listType));
        cronJobProfileWsDto.setMessage("Cronjob profile updated successfully");
        return cronJobProfileWsDto;
    }
}
