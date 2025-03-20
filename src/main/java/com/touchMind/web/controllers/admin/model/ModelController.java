package com.touchMind.web.controllers.admin.model;

import com.touchMind.core.mongo.dto.ModelDto;
import com.touchMind.core.mongo.dto.ModelWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Model;
import com.touchMind.core.mongo.model.Variant;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ModelRepository;
import com.touchMind.core.mongo.repository.VariantRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.ModelService;
import com.touchMind.core.service.SiteService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public ModelWsDto getAllActiveModels(@RequestBody ModelWsDto modelWsDto) {
        Pageable pageable = getPageable(modelWsDto.getPage(), modelWsDto.getSizePerPage(), modelWsDto.getSortDirection(), modelWsDto.getSortField());
        ModelDto modelDto = CollectionUtils.isNotEmpty(modelWsDto.getModels()) ? modelWsDto.getModels().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(modelDto, modelWsDto.getOperator());
        Model model = modelDto != null ? modelMapper.map(modelDto, Model.class) : null;
        Page<Model> page = isSearchActive(model) != null ? modelRepository.findAll(Example.of(model, exampleMatcher), pageable) : modelRepository.findAll(pageable);
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        modelWsDto.setModels(modelMapper.map(page.getContent(), listType));
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        modelWsDto.setTotalPages(page.getTotalPages());
        modelWsDto.setTotalRecords(page.getTotalElements());
        modelWsDto.setAttributeList(getConfiguredAttributes(modelWsDto.getNode()));
        modelWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.MODEL));
        return modelWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody ModelWsDto modelWsDto) {
        return getConfiguredAttributes(modelWsDto.getNode());
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Model());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.MODEL);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody ModelDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(modelRepository.findByIdentifier(recordId), ModelDto.class);
    }

    @GetMapping("/get")
    public ModelWsDto getActiveModels() {
        ModelWsDto modelWsDto = new ModelWsDto();
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        modelWsDto.setModels(modelMapper.map(modelRepository.findByStatusOrderByIdentifier(true), listType));
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
            List<Variant> variants = variantRepository.findAllByIdentifier(modelDto.getIdentifier());
            if (variants != null && !variants.isEmpty()) {
                modelWsDto.setMessage("There are variants exists for model id: " + modelDto.getIdentifier() + " first delete all the related variants try again !");
                return modelWsDto;
            }
            modelRepository.deleteByIdentifier(modelDto.getIdentifier());
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
            models.add(modelRepository.findByIdentifier(modelDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        modelWsDto.setModels(modelMapper.map(models, listType));
        modelWsDto.setRedirectUrl("/admin/model");
        modelWsDto.setBaseUrl(ADMIN_MODEL);
        return modelWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public ModelWsDto uploadFile(@RequestBody ModelWsDto modelWsDto) {

        try {
            modelWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MODEL, modelWsDto.getHeaderFields()));
            return modelWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
