package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.VariantDto;
import com.cheil.core.mongo.dto.VariantWsDto;
import com.cheil.core.mongo.model.Variant;
import com.cheil.core.mongo.repository.CategoryRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ModelRepository;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.mongo.repository.VariantRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.VariantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariantServiceImpl implements VariantService {

    public static final String ADMIN_VARIANT = "/admin/variant";

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private BaseService baseService;

    @Override
    public VariantWsDto handleEdit(VariantWsDto request) {
        VariantWsDto variantWsDto = new VariantWsDto();
        Variant requestData = null;
        List<VariantDto> variants = request.getVariants();
        for (VariantDto variantDto : variants) {
            if (variantDto.getRecordId() != null) {
                requestData = variantRepository.findByRecordId(variantDto.getRecordId());
                modelMapper.map(variantDto, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.VARIANT, variantDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(variantDto, Variant.class);
            }
            if (variantDto.getCategory() != null) {
                requestData.setCategory(categoryRepository.findByRecordId(variantDto.getCategory().getRecordId()));
            }
            if (variantDto.getModel() != null) {
                requestData.setModel(modelRepository.findByRecordId(variantDto.getModel().getRecordId()));
            }
            baseService.populateCommonData(requestData);
            variantRepository.save(requestData);
            if (variantDto.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            variantWsDto.setBaseUrl(ADMIN_VARIANT);
            variantRepository.save(requestData);
        }
        variantWsDto.setVariants(modelMapper.map(variants, List.class));
        return variantWsDto;
    }
}
