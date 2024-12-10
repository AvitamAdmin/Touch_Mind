package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.CronJobProfileDto;
import com.cheil.core.mongo.dto.CronJobProfileWsDto;
import com.cheil.core.mongo.model.CronJobProfile;
import com.cheil.core.mongo.repository.CronJobProfileRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.CronJobProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (cronJobProfile.getRecordId() != null) {
                requestData = cronJobProfileRepository.findByRecordId(String.valueOf(cronJobProfile.getRecordId()));
                modelMapper.map(cronJobProfile, requestData);

            } else {
                if (baseService.validateIdentifier(EntityConstants.CRONJOB_PROFILE, cronJobProfile.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(cronJobProfile, CronJobProfile.class);
            }
            baseService.populateCommonData(requestData);
            cronJobProfileRepository.save(requestData);
            if (cronJobProfile.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
            cronJobProfileRepository.save(requestData);
            cronJobProfileList.add(requestData);
        }
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileList, List.class));
        return cronJobProfileWsDto;
    }
}
