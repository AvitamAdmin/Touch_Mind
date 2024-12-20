package com.touchmind.web.controllers.admin.category;





import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
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

import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequestMapping("/admin/category")
    public class CategoryController extends BaseController {

        public static final String ADMIN_CATEGORY = "/admin/category";
        Logger logger = LoggerFactory.getLogger(CategoryController.class);


//        @Autowired
//        private FileImportService fileImportService;

//        @Autowired
//        private CategoryService categoryService;

//        @Autowired
//        private FileExportService fileExportService;

        @Autowired
        private ModelMapper modelMapper;}

//        @Autowired
//        private CategoryRepository categoryRepository;

//        @PostMapping
//        @ResponseBody
//        public CategoryWsDto getAllModels(@RequestBody CategoryWsDto categoryWsDto) {
//            Pageable pageable = getPageable(categoryWsDto.getPage(), categoryWsDto.getSizePerPage(), categoryWsDto.getSortDirection(), categoryWsDto.getSortField());
//            CategoryDto categoryDto = CollectionUtils.isNotEmpty(categoryWsDto.getCategories()) ? categoryWsDto.getCategories().get(0) : null;
//            ExampleMatcher exampleMatcher = getMatcher(categoryDto, categoryWsDto.getOperator());
//            Category category = categoryDto != null ? modelMapper.map(categoryDto, Category.class) : null;
//            Page<Category> page = isSearchActive(category) != null ? categoryRepository.findAll(Example.of(category, exampleMatcher), pageable) : categoryRepository.findAll(pageable);
//            categoryWsDto.setCategories(modelMapper.map(page.getContent(), List.class));
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            categoryWsDto.setTotalPages(page.getTotalPages());
//            categoryWsDto.setTotalRecords(page.getTotalElements());
//            categoryWsDto.setAttributeList(getConfiguredAttributes(categoryWsDto.getNode()));
//            return categoryWsDto;
//        }
//
//        @GetMapping("/getAdvancedSearch")
//        @ResponseBody
//        public List<SearchDto> getSearchAttributes() {
//            return getGroupedParentAndChildAttributes(new Category());
//        }
//
//        @GetMapping("/get")
//        @ResponseBody
//        public CategoryWsDto getActiveCategories() {
//            CategoryWsDto categoryWsDto = new CategoryWsDto();
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            categoryWsDto.setCategories(modelMapper.map(categoryRepository.findByStatusOrderByIdentifier(true), List.class));
//            return categoryWsDto;
//        }
//
//        @PostMapping("/edit")
//        @ResponseBody
//        public CategoryWsDto handleEdit(@RequestBody CategoryWsDto categoryWsDto) {
//            return categoryService.handleEdit(categoryWsDto);
//        }
//
//        @GetMapping("/add")
//        @ResponseBody
//        public CategoryWsDto addCategory() {
//            CategoryWsDto categoryWsDto = new CategoryWsDto();
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            categoryWsDto.setCategories(modelMapper.map(categoryRepository.findByStatus(true), List.class));
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            return categoryWsDto;
//        }
//
//        @PostMapping("/delete")
//        @ResponseBody
//        public CategoryWsDto deleteCategory(@RequestBody CategoryWsDto categoryWsDto) {
//            for (CategoryDto categoryDto : categoryWsDto.getCategories()) {
//                categoryRepository.deleteByRecordId(categoryDto.getRecordId());
//            }
//            categoryWsDto.setMessage("Data deleted successfully!!");
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            return categoryWsDto;
//        }
//
//        @PostMapping("/getedits")
//        @ResponseBody
//        public CategoryWsDto editMultiple(@RequestBody CategoryWsDto request) {
//            CategoryWsDto categoryWsDto = new CategoryWsDto();
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            List<Category> categoryList = new ArrayList<>();
//            for (CategoryDto categoryDto : request.getCategories()) {
//                categoryList.add(categoryRepository.findByRecordId(categoryDto.getRecordId()));
//            }
//            categoryWsDto.setCategories(modelMapper.map(categoryList, List.class));
//            categoryWsDto.setRedirectUrl("/admin/category");
//            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
//            return categoryWsDto;
//        }
//    }
//      //  @GetMapping("/export")
////        @ResponseBody
////        public CategoryWsDto uploadFile() {
////            CategoryWsDto categoryWsDto = new CategoryWsDto();
////            try {
////                categoryWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CATEGORY));
////                return categoryWsDto;
////            } catch (IOException e) {
////                logger.error(e.getMessage());
////                return null;
////            }
////        }
////
////        @PostMapping("/upload")
////        public CategoryWsDto uploadFile(@RequestBody MultipartFile file) {
////            CategoryWsDto categoryWsDto = new CategoryWsDto();
////            try {
////                fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.CATEGORY, EntityConstants.CATEGORY, categoryWsDto);
////                if (StringUtils.isEmpty(categoryWsDto.getMessage())) {
////                    categoryWsDto.setMessage("File uploaded successfully!!");
////                }
////            } catch (IOException e) {
////                logger.error(e.getMessage());
////            }
////            return categoryWsDto;
////        }
////    }

