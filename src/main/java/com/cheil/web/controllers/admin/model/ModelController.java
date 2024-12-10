package com.cheil.web.controllers.admin.model;

import com.cheil.core.mongo.dto.ModelDto;
import com.cheil.core.mongo.dto.ModelWsDto;
import com.cheil.core.mongo.dto.SearchDto;
import com.cheil.core.mongo.model.Model;
import com.cheil.core.mongo.model.Variant;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ModelRepository;
import com.cheil.core.mongo.repository.VariantRepository;
import com.cheil.core.service.ModelService;
import com.cheil.core.service.SiteService;
import com.cheil.fileimport.service.FileExportService;
import com.cheil.fileimport.service.FileImportService;
import com.cheil.fileimport.strategies.EntityType;
import com.cheil.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/model")
public class ModelController extends BaseController {

    public static final String ADMIN_MODEL = "/admin/model";
    Logger logger = LoggerFactory.getLogger(ModelController.class);
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private SiteService siteService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelService modelService;

    @PostMapping
    @ResponseBody
    public ModelWsDto getAllActiveModels(@RequestBody ModelWsDto modelWsDto) {
        Pageable pageable = getPageable(modelWsDto.getPage(), modelWsDto.getSizePerPage(), modelWsDto.getSortDirection(), modelWsDto.getSortField());
        ModelDto modelDto = CollectionUtils.isNotEmpty(modelWsDto.getModels()) ? modelWsDto.getModels().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(modelDto, modelWsDto.getOperator());
        Model model = modelDto != null ? modelMapper.map(modelDto, Model.class) : null;
        Page<Model> page = isSearchActive(model) != null ? modelRepository.findAll(Example.of(model, exampleMatcher), pageable) : modelRepository.findAll(pageable);
        modelWsDto.setModels(modelMapper.map(page.getContent(), List.class));
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        modelWsDto.setTotalPages(page.getTotalPages());
        modelWsDto.setTotalRecords(page.getTotalElements());
        modelWsDto.setAttributeList(getConfiguredAttributes(modelWsDto.getNode()));
        return modelWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Model());
    }

    @GetMapping("/get")
    public ModelWsDto getActiveModels() {
        ModelWsDto modelWsDto = new ModelWsDto();
        modelWsDto.setModels(modelMapper.map(modelRepository.findByStatusOrderByIdentifier(true), List.class));
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        return modelWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public ModelWsDto handleEdit(@RequestBody ModelWsDto request) {
        return modelService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public ModelWsDto addModel() {
        ModelWsDto modelWsDto = new ModelWsDto();
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        return modelWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ModelWsDto uploadFile(@RequestParam("file") MultipartFile file) {
        ModelWsDto modelWsDto = new ModelWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.MODEL, EntityConstants.MODEL, modelWsDto);
            if (StringUtils.isEmpty(modelWsDto.getMessage())) {
                modelWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return modelWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public ModelWsDto deleteModel(@RequestBody ModelWsDto modelWsDto) {
        for (ModelDto modelDto : modelWsDto.getModels()) {
            List<Variant> variants = variantRepository.findAllByIdentifier(Long.valueOf(modelDto.getRecordId()));
            if (variants != null && !variants.isEmpty()) {
                modelWsDto.setMessage("There are variants exists for model id: " + modelDto.getRecordId() + " first delete all the related variants try again !");
                return modelWsDto;
            }
            //TODO Check if this is correctly fetching the data
            modelRepository.deleteByRecordId(modelDto.getRecordId());
        }
        modelWsDto.setMessage("Data deleted successfully!!");
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        return modelWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ModelWsDto editMultiple(@RequestBody ModelWsDto request) {
        ModelWsDto modelWsDto = new ModelWsDto();
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        List<Model> models = new ArrayList<>();
        for (ModelDto modelDto : request.getModels()) {
            models.add(modelRepository.findByRecordId(modelDto.getRecordId()));
        }
        modelWsDto.setModels(modelMapper.map(models, List.class));
        modelWsDto.setRedirectUrl("/admin/model");
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        return modelWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public ModelWsDto uploadFile() {
        ModelWsDto modelWsDto = new ModelWsDto();
        try {
            modelWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MODEL));
            return modelWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
