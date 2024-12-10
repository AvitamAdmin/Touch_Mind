package com.touchmind.web.controllers.catalog;

import com.touchmind.core.mongo.dto.CatalogDto;
import com.touchmind.core.mongo.dto.CatalogWsDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SystemWsDto;
import com.touchmind.core.mongo.model.Catalog;
import com.touchmind.core.mongo.repository.CatalogRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.service.CatalogService;
import com.touchmind.core.service.CoreService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/catalog")
public class CatalogController extends BaseController {

    public static final String ADMIN_CATALOG = "/admin/catalog";

    Logger logger = LoggerFactory.getLogger(CatalogController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @PostMapping
    @ResponseBody
    public CatalogWsDto getAllCatalogs(@RequestBody CatalogWsDto catalogWsDto) throws IOException {
        Pageable pageable = getPageable(catalogWsDto.getPage(), catalogWsDto.getSizePerPage(), catalogWsDto.getSortDirection(), catalogWsDto.getSortField());
        CatalogDto catalogDto = CollectionUtils.isNotEmpty(catalogWsDto.getCatalogs()) ? catalogWsDto.getCatalogs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(catalogDto, catalogWsDto.getOperator());
        Catalog catalog = catalogDto != null ? modelMapper.map(catalogDto, Catalog.class) : null;
        Page<Catalog> page = isSearchActive(catalog) != null ? catalogRepository.findAll(Example.of(catalog, exampleMatcher), pageable) : catalogRepository.findAll(pageable);
        catalogWsDto.setCatalogs(modelMapper.map(page.getContent(), List.class));
        catalogWsDto.setBaseUrl(ADMIN_CATALOG);
        catalogWsDto.setTotalPages(page.getTotalPages());
        catalogWsDto.setTotalRecords(page.getTotalElements());
        catalogWsDto.setAttributeList(getConfiguredAttributes(catalogWsDto.getNode()));
        return catalogWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Catalog());
    }

    @GetMapping("/get")
    @ResponseBody
    public CatalogWsDto getActiveModules() {
        CatalogWsDto catalogWsDto = new CatalogWsDto();
        catalogWsDto.setBaseUrl(ADMIN_CATALOG);
        catalogWsDto.setCatalogs(modelMapper.map(catalogRepository.findByStatusOrderByIdentifier(true), List.class));
        return catalogWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CatalogWsDto handleEdit(@RequestBody CatalogWsDto request) throws IOException, InterruptedException {
        return catalogService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SystemWsDto addLibrary() {
        SystemWsDto systemWsDto = new SystemWsDto();
        systemWsDto.setBaseUrl(ADMIN_CATALOG);
        systemWsDto.setSystems(modelMapper.map(systemRepository.findByStatusOrderByIdentifier(true), List.class));
        return systemWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CatalogWsDto deleteLibrary(@RequestBody CatalogWsDto catalogWsDto) throws IOException, InterruptedException {
        try {
            for (CatalogDto catalogDto : catalogWsDto.getCatalogs()) {
                catalogRepository.deleteByRecordId(catalogDto.getRecordId());
            }
        } catch (Exception e) {
            return catalogWsDto;
        }
        catalogWsDto.setBaseUrl(ADMIN_CATALOG);
        catalogWsDto.setMessage("Data deleted successfully!!");
        return catalogWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public CatalogWsDto editMultiple(@RequestBody CatalogWsDto request) {
        CatalogWsDto catalogWsDto = new CatalogWsDto();
        List<Catalog> catalogList = new ArrayList<>();
        for (CatalogDto catalogDto : request.getCatalogs()) {
            catalogList.add(catalogRepository.findByRecordId(catalogDto.getRecordId()));
        }
        catalogWsDto.setCatalogs(modelMapper.map(catalogList, List.class));
        catalogWsDto.setBaseUrl(ADMIN_CATALOG);
        return catalogWsDto;
    }

    @PostMapping("/upload")
    public CatalogWsDto uploadFile(@RequestBody MultipartFile file) {
        CatalogWsDto catalogWsDto = new CatalogWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.CATALOG, EntityConstants.CATALOG, catalogWsDto);
            if (StringUtils.isEmpty(catalogWsDto.getMessage())) {
                catalogWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return catalogWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public CatalogWsDto uploadFile() {
        CatalogWsDto catalogWsDto = new CatalogWsDto();
        try {
            catalogWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CATALOG));
            return catalogWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
