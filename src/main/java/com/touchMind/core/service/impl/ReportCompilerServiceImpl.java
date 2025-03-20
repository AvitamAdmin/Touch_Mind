package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ReportCompilerDto;
import com.touchMind.core.mongo.dto.ReportCompilerWsDto;
import com.touchMind.core.mongo.model.ReportCompiler;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.ReportCompilerRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.ReportCompilerService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
        ReportCompiler requestData;
        for (ReportCompilerDto reportCompiler : reportCompilers) {
            if (reportCompiler.isAdd() && baseService.validateIdentifier(EntityConstants.REPORT_COMPILER, reportCompiler.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = reportCompilerRepository.findByIdentifier(reportCompiler.getIdentifier());
            if (requestData != null) {
                modelMapper.map(reportCompiler, requestData);
            } else {
                requestData = modelMapper.map(reportCompiler, ReportCompiler.class);
            }
            baseService.populateCommonData(requestData);
            reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
            reportCompilerRepository.save(requestData);
            reportCompilerList.add(requestData);
        }
        reportCompilerWsDto.setMessage("Report Compiler updated successfully");
        Type listType = new TypeToken<List<ReportCompilerDto>>() {
        }.getType();
        reportCompilerWsDto.setReportCompilers(modelMapper.map(reportCompilerList, listType));
        return reportCompilerWsDto;
    }
}
