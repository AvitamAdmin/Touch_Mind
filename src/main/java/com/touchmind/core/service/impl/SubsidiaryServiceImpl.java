package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SubsidiaryDto;
import com.touchmind.core.mongo.dto.SubsidiaryWsDto;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.CountryRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.SubsidiaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubsidiaryServiceImpl implements SubsidiaryService {

    public static final String ADMIN_SUBSIDIARY = "/admin/subsidiary";

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private BaseService baseService;

    @Override
    public List<Subsidiary> findByStatusAndUserOrderByIdentifier(Boolean status) {
        return subsidiaryRepository.findByStatusOrderByIdentifier(true);
    }

    @Override
    public SubsidiaryWsDto handleEdit(SubsidiaryWsDto request) {
        SubsidiaryWsDto subsidiaryWsDto = new SubsidiaryWsDto();
        subsidiaryWsDto.setCountries(modelMapper.map(countryRepository.findByStatusOrderByIdentifier(true), List.class));
        List<SubsidiaryDto> subsidiaries = request.getSubsidiaries();
        List<Subsidiary> subsidiaryList = new ArrayList<>();
        Subsidiary requestData = null;
        for (SubsidiaryDto subsidiary : subsidiaries) {
            if (subsidiary.getRecordId() != null) {
                requestData = subsidiaryRepository.findByRecordId(subsidiary.getRecordId());
                modelMapper.map(subsidiary, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SUBSIDIARY, subsidiary.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(subsidiary, Subsidiary.class);
            }
            baseService.populateCommonData(requestData);
            subsidiaryRepository.save(requestData);
            if (subsidiary.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            subsidiaryRepository.save(requestData);
            subsidiaryList.add(requestData);
            subsidiaryWsDto.setBaseUrl(ADMIN_SUBSIDIARY);
        }
        subsidiaryWsDto.setSubsidiaries(modelMapper.map(subsidiaryList, List.class));
        return subsidiaryWsDto;
    }
}
