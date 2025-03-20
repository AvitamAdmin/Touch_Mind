package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.VariantDto;
import com.touchMind.core.mongo.dto.VariantWsDto;
import com.touchMind.core.mongo.model.Variant;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ModelRepository;
import com.touchMind.core.mongo.repository.VariantRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.VariantService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
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
            if (variantDto.isAdd() && baseService.validateIdentifier(EntityConstants.VARIANT, variantDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = variantRepository.findByIdentifier(variantDto.getIdentifier());
            if (requestData != null) {
                modelMapper.map(variantDto, requestData);
            } else {
                requestData = modelMapper.map(variantDto, Variant.class);
            }
            if (variantDto.getCategory() != null) {
                requestData.setCategory(categoryRepository.findByIdentifier(variantDto.getCategory().getIdentifier()));
            }
            if (variantDto.getModel() != null) {
                requestData.setModel(modelRepository.findByIdentifier(variantDto.getModel().getIdentifier()));
            }
            baseService.populateCommonData(requestData);
            variantWsDto.setBaseUrl(ADMIN_VARIANT);
            variantRepository.save(requestData);
        }
        Type listType = new TypeToken<List<VariantDto>>() {
        }.getType();
        variantWsDto.setVariants(modelMapper.map(variants, listType));
        variantWsDto.setMessage("Variant updated successfully");
        return variantWsDto;
    }
}
