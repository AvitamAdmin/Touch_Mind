package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.LibraryDto;
import com.cheil.core.mongo.dto.LibraryWsDto;
import com.cheil.core.mongo.model.Library;
import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.LibraryRepository;
import com.cheil.core.mongo.repository.SiteRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.LibraryService;
import com.cheil.core.service.SubsidiaryService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryServiceImpl implements LibraryService {

    public static final String ADMIN_LIBRARY = "/admin/library";
    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public LibraryWsDto handleEdit(LibraryWsDto request) {
        LibraryWsDto libraryWsDto = new LibraryWsDto();
        List<Subsidiary> subsidiaries = subsidiaryService.findByStatusAndUserOrderByIdentifier(true);
        Map<String, List<Site>> subSiteMap = new HashMap<>();
        for (Subsidiary subsidiary : subsidiaries) {
            List<Site> sites = siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary.getRecordId(), true);
            if (CollectionUtils.isNotEmpty(sites)) {
                subSiteMap.put(subsidiary.getIdentifier(), sites);
            }
        }
        libraryWsDto.setSubSiteMap(subSiteMap);
        Library requestData = null;
        List<LibraryDto> libraries = request.getLibraries();
        List<Library> libraryList = new ArrayList<>();
        for (LibraryDto library : libraries) {
            if (library.getRecordId() != null) {
                requestData = libraryRepository.findByRecordId(library.getRecordId());
                modelMapper.map(library, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.LIBRARY, library.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(library, Library.class);
            }
            baseService.populateCommonData(requestData);
            libraryRepository.save(requestData);
            if (library.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            libraryRepository.save(requestData);
            libraryList.add(requestData);
            libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
            libraryWsDto.setMessage("Library was updated successfully");
        }
        libraryWsDto.setLibraries(modelMapper.map(libraryList, List.class));
        return libraryWsDto;
    }
}
