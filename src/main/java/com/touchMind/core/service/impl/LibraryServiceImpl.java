package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.LibraryDto;
import com.touchMind.core.mongo.dto.LibraryWsDto;
import com.touchMind.core.mongo.model.Library;
import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LibraryRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.LibraryService;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryServiceImpl implements LibraryService {

    public static final String ADMIN_LIBRARY = "/admin/library";
//    @Autowired
//    private SubsidiaryService subsidiaryService;

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
      //  List<Subsidiary> subsidiaries = subsidiaryService.findByStatusAndUserOrderByIdentifier(true);
        Map<String, List<Site>> subSiteMap = new HashMap<>();
//        for (Subsidiary subsidiary : subsidiaries) {
//            List<Site> sites = siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary.getIdentifier(), true);
//            if (CollectionUtils.isNotEmpty(sites)) {
//                subSiteMap.put(subsidiary.getIdentifier(), sites);
//            }
//        }
        libraryWsDto.setSubSiteMap(subSiteMap);
        Library requestData = null;
        List<LibraryDto> libraries = request.getLibraries();
        List<Library> libraryList = new ArrayList<>();
        for (LibraryDto library : libraries) {
            if (library.isAdd() && baseService.validateIdentifier(EntityConstants.LIBRARY, library.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = libraryRepository.findByIdentifier(library.getIdentifier());
            if (requestData != null) {
                modelMapper.map(library, requestData);
            } else {
                requestData = modelMapper.map(library, Library.class);
            }
            baseService.populateCommonData(requestData);
            libraryRepository.save(requestData);
            libraryList.add(requestData);
            libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
            libraryWsDto.setMessage("Library updated successfully");
        }
        Type listType = new TypeToken<List<LibraryDto>>() {
        }.getType();
        libraryWsDto.setLibraries(modelMapper.map(libraryList, listType));
        return libraryWsDto;
    }
}
