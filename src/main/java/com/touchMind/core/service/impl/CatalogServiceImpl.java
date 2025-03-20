package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CatalogDto;
import com.touchMind.core.mongo.dto.CatalogWsDto;
import com.touchMind.core.mongo.model.Catalog;
import com.touchMind.core.mongo.repository.CatalogRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CatalogService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    public static final String ADMIN_CATALOG = "/admin/catalog";
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Override
    public CatalogWsDto handleEdit(CatalogWsDto request) {
        CatalogWsDto catalogWsDto = new CatalogWsDto();
        List<CatalogDto> catalogs = request.getCatalogs();
        List<Catalog> catalogList = new ArrayList<>();
        Catalog requestData = null;
        for (CatalogDto catalog : catalogs) {
            if (catalog.isAdd() && baseService.validateIdentifier(EntityConstants.CATALOG, catalog.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = catalogRepository.findByIdentifier(catalog.getIdentifier());
            if (requestData != null) {
                modelMapper.map(catalog, requestData);
            } else {
                requestData = modelMapper.map(catalog, Catalog.class);
            }
            if (catalog.getSystem() != null) {
                requestData.setSystem(systemRepository.findByIdentifier(catalog.getSystem().getIdentifier()));
            }
            baseService.populateCommonData(requestData);
            catalogRepository.save(requestData);
            catalogWsDto.setBaseUrl(ADMIN_CATALOG);
            catalogList.add(requestData);
        }
        Type listType = new TypeToken<List<CatalogDto>>() {
        }.getType();
        catalogWsDto.setCatalogs(modelMapper.map(catalogList, listType));
        catalogWsDto.setMessage("Catalog updated successfully!!");
        return catalogWsDto;
    }
}
