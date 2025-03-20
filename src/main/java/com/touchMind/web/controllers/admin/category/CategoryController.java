package com.touchMind.web.controllers.admin.category;

import com.touchMind.core.mongo.dto.CategoryDto;
import com.touchMind.core.mongo.dto.CategoryWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Category;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CategoryService;
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
@RequestMapping("/admin/category")
public class CategoryController extends BaseController {

    public static final String ADMIN_CATEGORY = "/admin/category";
    Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @PostMapping
    @ResponseBody
    public CategoryWsDto getAllModels(@RequestBody CategoryWsDto categoryWsDto) {
        Pageable pageable = getPageable(categoryWsDto.getPage(), categoryWsDto.getSizePerPage(), categoryWsDto.getSortDirection(), categoryWsDto.getSortField());
        CategoryDto categoryDto = CollectionUtils.isNotEmpty(categoryWsDto.getCategories()) ? categoryWsDto.getCategories().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(categoryDto, categoryWsDto.getOperator());
        Category category = categoryDto != null ? modelMapper.map(categoryDto, Category.class) : null;
        Page<Category> page = isSearchActive(category) != null ? categoryRepository.findAll(Example.of(category, exampleMatcher), pageable) : categoryRepository.findAll(pageable);
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        categoryWsDto.setCategories(modelMapper.map(page.getContent(), listType));
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        categoryWsDto.setTotalPages(page.getTotalPages());
        categoryWsDto.setTotalRecords(page.getTotalElements());
        categoryWsDto.setAttributeList(getConfiguredAttributes(categoryWsDto.getNode()));
        categoryWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.CATEGORY));
        return categoryWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody CategoryWsDto categoryWsDto) {
        return getConfiguredAttributes(categoryWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Category());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.CATEGORY);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CategoryDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(categoryRepository.findByIdentifier(recordId), CategoryDto.class);
    }

    @GetMapping("/get")
    @ResponseBody
    public CategoryWsDto getActiveCategories() {
        CategoryWsDto categoryWsDto = new CategoryWsDto();
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        categoryWsDto.setCategories(modelMapper.map(categoryRepository.findByStatusOrderByIdentifier(true), listType));
        return categoryWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CategoryWsDto handleEdit(@RequestBody CategoryWsDto categoryWsDto) {
        return categoryService.handleEdit(categoryWsDto);
    }

    @GetMapping("/add")
    @ResponseBody
    public CategoryWsDto addCategory() {
        CategoryWsDto categoryWsDto = new CategoryWsDto();
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        categoryWsDto.setCategories(modelMapper.map(categoryRepository.findByStatus(true), listType));
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        return categoryWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CategoryWsDto deleteCategory(@RequestBody CategoryWsDto categoryWsDto) {
        for (CategoryDto categoryDto : categoryWsDto.getCategories()) {
            categoryRepository.deleteByIdentifier(categoryDto.getIdentifier());
        }
        categoryWsDto.setMessage("Data deleted successfully!!");
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        return categoryWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public CategoryWsDto editMultiple(@RequestBody CategoryWsDto request) {
        CategoryWsDto categoryWsDto = new CategoryWsDto();
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        List<Category> categoryList = new ArrayList<>();
        for (CategoryDto categoryDto : request.getCategories()) {
            categoryList.add(categoryRepository.findByIdentifier(categoryDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        categoryWsDto.setCategories(modelMapper.map(categoryList, listType));
        categoryWsDto.setRedirectUrl("/admin/category");
        categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
        return categoryWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public CategoryWsDto uploadFile(@RequestBody CategoryWsDto categoryWsDto) {

        try {
            categoryWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CATEGORY, categoryWsDto.getHeaderFields()));
            return categoryWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    public CategoryWsDto uploadFile(@RequestBody MultipartFile file) {
        CategoryWsDto categoryWsDto = new CategoryWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.CATEGORY, EntityConstants.CATEGORY, categoryWsDto);
            if (StringUtils.isEmpty(categoryWsDto.getMessage())) {
                categoryWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return categoryWsDto;
    }
}
