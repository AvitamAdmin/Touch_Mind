package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ReportsMapperDto;
import com.touchMind.core.mongo.dto.ReportsMapperWsDto;
import com.touchMind.core.mongo.model.ReportsMapper;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ReportsMapperRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.ReportsMapperService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ReportsMapperServiceImpl implements ReportsMapperService {

    public static final String ADMIN_REPORT_MAPPER = "/admin/reportMapper";
    @Autowired
    private BaseService baseService;
    @Autowired
    private ReportsMapperRepository reportsMapperRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;


    @Override
    public List<ReportsMapper> findByStatusOrderByIdentifier(Boolean status) {
        return reportsMapperRepository.findByStatusOrderByIdentifier(status);
    }

    @Override
    public ReportsMapperWsDto handleEdit(ReportsMapperWsDto request) {
        ReportsMapperWsDto reportsMapperWsDto = new ReportsMapperWsDto();
        ReportsMapper reportsMapper = null;
        List<ReportsMapperDto> list = request.getReportsMapperDtoList();
        for (ReportsMapperDto reportsMapperDto : list) {
            if (reportsMapperDto.isAdd() && baseService.validateIdentifier(EntityConstants.REPORT_MAPPER, reportsMapperDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            reportsMapper = reportsMapperRepository.findByIdentifier(reportsMapperDto.getIdentifier());
            if (reportsMapper != null) {
                modelMapper.map(reportsMapperDto, reportsMapper);
            } else {
                reportsMapper = modelMapper.map(reportsMapperDto, ReportsMapper.class);
            }
            baseService.populateCommonData(reportsMapper);
            reportsMapperRepository.save(reportsMapper);
            reportsMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        }
        Type listType = new TypeToken<List<ReportsMapperDto>>() {
        }.getType();
        reportsMapperWsDto.setReportsMapperDtoList(modelMapper.map(reportsMapper, listType));
        reportsMapperWsDto.setMessage("Report Mapper updated successfully");
        return reportsMapperWsDto;
    }
}
