package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.CatalogDto;
import com.touchmind.core.mongo.dto.CatalogWsDto;
import com.touchmind.core.mongo.model.Catalog;
import com.touchmind.core.mongo.repository.CatalogRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CatalogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (catalog.getRecordId() != null) {
                requestData = catalogRepository.findByRecordId(catalog.getRecordId());
                modelMapper.map(catalog, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.CATALOG, catalog.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(catalog, Catalog.class);
            }
            if (catalog.getSystem() != null) {
                requestData.setSystem(systemRepository.findByRecordId(catalog.getSystem().getRecordId()));
            }
            baseService.populateCommonData(requestData);
            catalogRepository.save(requestData);
            if (requestData.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            catalogRepository.save(requestData);
            catalogWsDto.setBaseUrl(ADMIN_CATALOG);
            catalogList.add(requestData);
        }
        catalogWsDto.setCatalogs(modelMapper.map(catalogList, List.class));
        catalogWsDto.setMessage("Catalog was updated successfully!!");
        return catalogWsDto;
    }
}
