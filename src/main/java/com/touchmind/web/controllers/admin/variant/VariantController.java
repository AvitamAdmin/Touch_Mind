package com.touchmind.web.controllers.admin.variant;


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
@RequestMapping("/admin/variant")
public class VariantController extends BaseController {

    public static final String ADMIN_VARIANT = "/admin/variant";

    Logger logger = LoggerFactory.getLogger(VariantController.class);
//    @Autowired
//    private VariantRepository variantRepository;
////    @Autowired
////    private FileImportService fileImportService;
//    @Autowired
//    private VariantService variantService;
//    @Autowired
//    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;}

//    @PostMapping
//    @ResponseBody
//    public VariantWsDto getAllVariants(@RequestBody VariantWsDto variantWsDto) {
//        Pageable pageable = getPageable(variantWsDto.getPage(), variantWsDto.getSizePerPage(), variantWsDto.getSortDirection(), variantWsDto.getSortField());
//        VariantDto variantDto = CollectionUtils.isNotEmpty(variantWsDto.getVariants()) ? variantWsDto.getVariants().get(0) : null;
//        ExampleMatcher exampleMatcher = getMatcher(variantDto, variantWsDto.getOperator());
//        Variant variant = variantDto != null ? modelMapper.map(variantDto, Variant.class) : null;
//        Page<Variant> page = isSearchActive(variant) != null ? variantRepository.findAll(Example.of(variant, exampleMatcher), pageable) : variantRepository.findAll(pageable);
//        variantWsDto.setVariants(modelMapper.map(page.getContent(), List.class));
//        variantWsDto.setBaseUrl(ADMIN_VARIANT);
//        variantWsDto.setTotalPages(page.getTotalPages());
//        variantWsDto.setTotalRecords(page.getTotalElements());
//        variantWsDto.setAttributeList(getConfiguredAttributes(variantWsDto.getNode()));
//        return variantWsDto;
//    }
//
//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new Variant());
//    }
//
//    @GetMapping("/get")
//    @ResponseBody
//    public VariantWsDto getActiveVariants() {
//        VariantWsDto variantWsDto = new VariantWsDto();
//        variantWsDto.setBaseUrl(ADMIN_VARIANT);
//        variantWsDto.setVariants(modelMapper.map(variantRepository.findByStatusOrderByIdentifier(true), List.class));
//        return variantWsDto;
//    }

//    @PostMapping("/upload")
//    @ResponseBody
//    public VariantWsDto uploadFile(@RequestBody MultipartFile file) {
//        VariantWsDto variantWsDto = new VariantWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.VARIANT, EntityConstants.VARIANT, variantWsDto);
//            if (StringUtils.isEmpty(variantWsDto.getMessage())) {
//                variantWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return variantWsDto;
//    }

//    @PostMapping("/edit")
//    @ResponseBody
//    public VariantWsDto handleEdit(@RequestBody VariantWsDto request) {
//        return variantService.handleEdit(request);
//    }
//
//    @GetMapping("/add")
//    @ResponseBody
//    public VariantWsDto addCategory() {
//        VariantWsDto variantWsDto = new VariantWsDto();
//        variantWsDto.setBaseUrl(ADMIN_VARIANT);
//        return variantWsDto;
//    }
//
//    @PostMapping("/delete")
//    @ResponseBody
//    public VariantWsDto deleteCategory(@RequestBody VariantWsDto variantWsDto) {
//        for (VariantDto variantDto : variantWsDto.getVariants()) {
//            variantRepository.deleteByRecordId(variantDto.getRecordId());
//        }
//        variantWsDto.setBaseUrl(ADMIN_VARIANT);
//        variantWsDto.setMessage("Data deleted successfully!!");
//        return variantWsDto;
//    }
//
//    @PostMapping("/getedits")
//    @ResponseBody
//    public VariantWsDto editMultiple(@RequestBody VariantWsDto request) {
//        VariantWsDto variantWsDto = new VariantWsDto();
//        List<Variant> variants = new ArrayList<>();
//        for (VariantDto variantDto : request.getVariants()) {
//            variants.add(variantRepository.findByRecordId(variantDto.getRecordId()));
//        }
//        variantWsDto.setVariants(modelMapper.map(variants, List.class));
//        variantWsDto.setBaseUrl(ADMIN_VARIANT);
//        return variantWsDto;
//    }
//
////    @GetMapping("/export")
////    @ResponseBody
////    public VariantWsDto uploadFile() {
////        VariantWsDto variantWsDto = new VariantWsDto();
////        try {
////            variantWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.VARIANT));
////            return variantWsDto;
////        } catch (IOException e) {
////            logger.error(e.getMessage());
////            return null;
////        }
////    }
//}
