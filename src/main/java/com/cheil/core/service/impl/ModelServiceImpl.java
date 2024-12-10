package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ModelDto;
import com.cheil.core.mongo.dto.ModelWsDto;
import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.model.Model;
import com.cheil.core.mongo.model.Variant;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ModelRepository;
import com.cheil.core.mongo.repository.VariantRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.ModelService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

@Service
public class ModelServiceImpl implements ModelService {

    public static final String ADMIN_MODEL = "/admin/model";

    public static final String PREFIX = ",";
    private static final String DELIM = "\n\r\t;";
    private static final String DELIM2 = "\n\r\t;";
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public String getVariantAsCommaSeparatedList(ReportDto reportDto) {
        StringBuffer buffer = new StringBuffer();
        if (StringUtils.isNotEmpty(reportDto.getSkus())) {
            StringTokenizer st = new StringTokenizer(reportDto.getSkus(), (reportDto.getBundle() != null && reportDto.getBundle()) ? DELIM2 : DELIM);
            String prefix = "";
            while (st.hasMoreTokens()) {
                buffer.append(prefix);
                prefix = PREFIX;
                buffer.append(st.nextToken());
            }
        }
        return buffer.toString();
    }

    @Override
    public String getVariantAsCommaSeparatedListForModelIds(List<String> models) {
        StringBuffer variantsBuf = new StringBuffer();
        if (models != null && models.isEmpty()) {
            String prefix = "";
            for (String modelId : models) {
                Model model = modelRepository.findByIdentifier(modelId);
                Set<String> variants = model.getVariants();
                for (String variant : variants) {
                    variantsBuf.append(prefix);
                    prefix = PREFIX;
                    variantsBuf.append(variantRepository.findByRecordId(variant).getIdentifier());
                }
            }
        }
        return variantsBuf.toString();
    }

    @Override
    public List<String> getVariantsListForModels(List<String> models) {
        List<String> variantList = new ArrayList<>();
        if (models != null && !models.isEmpty()) {
            for (String modelId : models) {
                List<Variant> variants = variantRepository.findAllByIdentifier(Long.valueOf(modelId));
                for (Variant variant : variants) {
                    variantList.add(variant.getIdentifier());
                }
            }
        }
        return variantList;
    }

    @Override
    public List<String> getVariantListForCommaSeparatedSkus(ReportDto reportDto) {
        List<String> variantsList = new ArrayList<>();
        if (StringUtils.isNotEmpty(reportDto.getSkus())) {
            StringTokenizer st = new StringTokenizer(reportDto.getSkus(), (reportDto.getBundle() != null && reportDto.getBundle()) ? DELIM2 : DELIM);
            while (st.hasMoreTokens()) {
                variantsList.add(st.nextToken());
            }
        }
        return variantsList;
    }

    @Override
    public List<Model> findAll() {
        return modelRepository.findAll();
    }

    @Override
    public List<Model> findBySubsidiaryId(String id) {
        //TODO fix this method
        return null;
        //modelRepository.findBySubsidiaryIdAndStatusOrderByShortDescription(id, true);
    }

    @Override
    public ModelWsDto handleEdit(ModelWsDto request) {
        ModelWsDto modelWsDto = new ModelWsDto();
        Model requestData = null;
        List<ModelDto> models = request.getModels();
        List<Model> modelList = new ArrayList<>();
        for (ModelDto model : models) {
            if (model.getRecordId() != null) {
                requestData = modelRepository.findByRecordId(model.getRecordId());
                modelMapper.map(model, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.MODEL, model.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(model, Model.class);
            }
            baseService.populateCommonData(requestData);
            modelRepository.save(requestData);
            if (model.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            modelRepository.save(requestData);
            modelList.add(requestData);
            modelWsDto.setMessage("Model was updated successfully!!");
            modelWsDto.setBaseUrl(ADMIN_MODEL);
        }
        modelWsDto.setModels(modelMapper.map(modelList, List.class));
        return modelWsDto;
    }
}
