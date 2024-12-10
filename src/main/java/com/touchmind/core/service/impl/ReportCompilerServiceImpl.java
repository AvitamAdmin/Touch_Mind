package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.ReportCompilerDto;
import com.touchmind.core.mongo.dto.ReportCompilerWsDto;
import com.touchmind.core.mongo.model.ReportCompiler;
import com.touchmind.core.mongo.repository.DataRelationRepository;
import com.touchmind.core.mongo.repository.DataSourceRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.ReportCompilerRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.ReportCompilerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportCompilerServiceImpl implements ReportCompilerService {

    public static final String ADMIN_REPORTCOMPILER = "/admin/reportCompiler";

    @Autowired
    private DataRelationRepository dataRelationRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ReportCompilerRepository reportCompilerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public ReportCompilerWsDto handleEdit(ReportCompilerWsDto request) {
        ReportCompilerWsDto reportCompilerWsDto = new ReportCompilerWsDto();
        List<ReportCompilerDto> reportCompilers = request.getReportCompilers();
        List<ReportCompiler> reportCompilerList = new ArrayList<>();
        ReportCompiler requestData = null;
        for (ReportCompilerDto reportCompiler : reportCompilers) {
            if (reportCompiler.getRecordId() != null) {
                requestData = reportCompilerRepository.findByRecordId(reportCompiler.getRecordId());
                modelMapper.map(reportCompiler, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.REPORT_COMPILER, reportCompiler.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(reportCompiler, ReportCompiler.class);
            }
            baseService.populateCommonData(requestData);
            reportCompilerRepository.save(requestData);
            if (reportCompiler.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
            reportCompilerRepository.save(requestData);
            reportCompilerList.add(requestData);
        }
        reportCompilerWsDto.setReportCompilers(modelMapper.map(reportCompilerList, List.class));
        return reportCompilerWsDto;
    }
}
