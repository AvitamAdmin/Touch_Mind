//package com.touchMind.web.controllers.admin.library;
//
//import com.touchMind.core.mongo.dto.LibraryDto;
//import com.touchMind.core.mongo.dto.LibraryWsDto;
//import com.touchMind.core.mongo.dto.SavedQueryDto;
//import com.touchMind.core.mongo.dto.SearchDto;
//import com.touchMind.core.mongo.model.Action;
//import com.touchMind.core.mongo.model.Library;
//import com.touchMind.core.mongo.model.Site;
//import com.touchMind.core.mongo.repository.ActionRepository;
//import com.touchMind.core.mongo.repository.EntityConstants;
//import com.touchMind.core.mongo.repository.LibraryRepository;
//import com.touchMind.core.mongo.repository.MediaRepository;
//import com.touchMind.core.mongo.repository.SiteRepository;
//import com.touchMind.core.service.BaseService;
//import com.touchMind.core.service.LibraryService;
//import com.touchMind.fileimport.service.FileExportService;
//import com.touchMind.fileimport.service.FileImportService;
//import com.touchMind.fileimport.strategies.EntityType;
//import com.touchMind.web.controllers.BaseController;
//import com.touchMind.web.controllers.admin.model.ModelController;
//import com.google.common.reflect.TypeToken;
//import jakarta.servlet.http.HttpServletRequest;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.ExampleMatcher;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@RestController
//@RequestMapping("/admin/library")
//public class LibraryController extends BaseController {
//    public static final String ADMIN_LIBRARY = "/admin/library";
//    Logger logger = LoggerFactory.getLogger(ModelController.class);
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private Environment env;
//
//    @Autowired
//    private LibraryRepository libraryRepository;
//
//    @Autowired
//    private MediaRepository mediaRepository;
//
//    @Autowired
//    private ActionRepository actionRepository;
//
//    @Autowired
//    private SiteRepository siteRepository;
//
////    @Autowired
////    private SubsidiaryService subsidiaryService;
//
//    @Autowired
//    private LibraryService libraryService;
//
//    @Autowired
//    private FileImportService fileImportService;
//
//    @Autowired
//    private FileExportService fileExportService;
//    @Autowired
//    private BaseService baseService;
//
//    @PostMapping
//    @ResponseBody
//    public LibraryWsDto getAllLibraries(@RequestBody LibraryWsDto libraryWsDto) throws IOException {
//        Pageable pageable = getPageable(libraryWsDto.getPage(), libraryWsDto.getSizePerPage(), libraryWsDto.getSortDirection(), libraryWsDto.getSortField());
//        LibraryDto libraryDto = CollectionUtils.isNotEmpty(libraryWsDto.getLibraries()) ? libraryWsDto.getLibraries().get(0) : null;
//        ExampleMatcher exampleMatcher = getMatcher(libraryDto, libraryWsDto.getOperator());
//        Library library = libraryDto != null ? modelMapper.map(libraryDto, Library.class) : null;
//        Page<Library> page = isSearchActive(library) != null ? libraryRepository.findAll(Example.of(library, exampleMatcher), pageable) : libraryRepository.findAll(pageable);
//        Type listType = new TypeToken<List<LibraryDto>>() {
//        }.getType();
//        libraryWsDto.setLibraries(modelMapper.map(page.getContent(), listType));
//        libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
//        libraryWsDto.setTotalPages(page.getTotalPages());
//        libraryWsDto.setTotalRecords(page.getTotalElements());
//        libraryWsDto.setAttributeList(getConfiguredAttributes(libraryWsDto.getNode()));
//        libraryWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.LIBRARY));
//        return libraryWsDto;
//    }
//
//    @PostMapping("/getSearchQuery")
//    @ResponseBody
//    public List<SearchDto> savedQuery(@RequestBody LibraryWsDto libraryWsDto) {
//        return getConfiguredAttributes(libraryWsDto.getNode());
//    }
//
//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new Library());
//    }
//
//    @PostMapping("/saveSearchQuery")
//    @ResponseBody
//    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
//        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.LIBRARY);
//    }
//
//    @GetMapping("/get")
//    @ResponseBody
//    public LibraryWsDto getActiveLibraries() {
//        LibraryWsDto libraryWsDto = new LibraryWsDto();
//        Type listType = new TypeToken<List<LibraryDto>>() {
//        }.getType();
//        libraryWsDto.setLibraries(modelMapper.map(libraryRepository.findByStatusOrderByIdentifier(true), listType));
//        libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
//        return libraryWsDto;
//    }
//
//    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
//    public @ResponseBody LibraryDto getByIdentifier(@RequestParam("recordId") String recordId) {
//        return modelMapper.map(libraryRepository.findByIdentifier(recordId), LibraryDto.class);
//    }
//
//    @PostMapping("/getedits")
//    @ResponseBody
//    public LibraryWsDto editsLibrary(@RequestBody LibraryWsDto request) throws IOException {
//
//        LibraryWsDto libraryWsDto = new LibraryWsDto();
//        libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
//        libraryWsDto.setBackUrl("/admin/library/edits?id=" + request.getLibId() + "&libId=");
//        List<Library> libraries = new ArrayList<>();
//        for (LibraryDto libraryDto : request.getLibraries()) {
//            libraries.add(libraryRepository.findByIdentifier(libraryDto.getIdentifier()));
//        }
//        Type listType = new TypeToken<List<LibraryDto>>() {
//        }.getType();
//        libraryWsDto.setLibraries(modelMapper.map(libraries, listType));
//        libraryWsDto.setTypes(env.getProperty("library.types").split(","));
//        libraryWsDto.setRedirectUrl("/admin/library");
//        return libraryWsDto;
//    }
//
//    @GetMapping("/saveremarks")
//    @ResponseBody
//    public void updateRemarks(@RequestParam(defaultValue = "") String libId, @RequestParam(defaultValue = "") String remarks, HttpServletRequest request, Model model) throws IOException {
//
//        if (StringUtils.isNotEmpty(libId)) {
//            Library library = libraryRepository.findByIdentifier(libId);
//            if (library != null) {
//                List<String> actionRemarks = library.getActionRemarks();
//                List<String> remarksList = new ArrayList<>();
//                if (CollectionUtils.isNotEmpty(actionRemarks)) {
//                    remarksList.addAll(actionRemarks);
//                    if (!actionRemarks.contains(remarks)) {
//                        remarksList.add(remarks);
//                    }
//                } else {
//                    remarksList.add(remarks);
//                }
//
//                library.setActionRemarks(remarksList);
//                try {
//                    libraryRepository.save(library);
//                } catch (Exception e) {
//                }
//            }
//        }
//    }
//
//    @GetMapping("/addLib")
//    @ResponseBody
//    public List<Library> editLibraryAjax(@RequestParam(defaultValue = "") String libId, HttpServletRequest request, Model model) throws IOException {
//        List<Library> subLibraries = new ArrayList<>();
//        if (StringUtils.isNotEmpty(libId)) {
//            for (String subId : libId.split(",")) {
//                Library subLib = libraryRepository.findByIdentifier(subId);
//                if (subLib != null) {
//                    subLibraries.add(subLib);
//                }
//            }
//        }
//        subLibraries.sort(Comparator.comparing(action -> action.getLastModified()));
//        return subLibraries;
//    }
//
//    @GetMapping("/addAction")
//    @ResponseBody
//    public List<Action> editLibraryActionAjax(@RequestParam(defaultValue = "") String actionId, HttpServletRequest request, Model model) throws IOException {
//        List<Action> actions = new ArrayList<>();
//        if (StringUtils.isNotEmpty(actionId)) {
//            for (String subId : actionId.split(",")) {
//                Action action = actionRepository.findByIdentifier(subId);
//                if (action != null) {
//                    actions.add(action);
//                }
//            }
//        }
//        actions.sort(Comparator.comparing(action -> action.getLastModified()));
//        return actions;
//    }
//
//    @PostMapping("/edit")
//    @ResponseBody
//    public LibraryWsDto handleEdit(@RequestBody LibraryWsDto request) throws IOException, InterruptedException {
//        return libraryService.handleEdit(request);
//    }
//
//    @GetMapping("/add")
//    @ResponseBody
//    public LibraryWsDto addLibrary() {
//        LibraryWsDto libraryWsDto = new LibraryWsDto();
//        libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
//        libraryWsDto.setTypes(env.getProperty("library.types").split(","));
//        List<Subsidiary> subsidiaries = subsidiaryService.findByStatusAndUserOrderByIdentifier(true);
//        Map<String, List<Site>> subSiteMap = new HashMap<>();
//        for (Subsidiary subsidiary : subsidiaries) {
//            List<Site> sites = siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary.getIdentifier(), true);
//            if (CollectionUtils.isNotEmpty(sites)) {
//                subSiteMap.put(subsidiary.getIdentifier(), sites);
//            }
//        }
//        libraryWsDto.setSubSiteMap(subSiteMap);
//        List<Library> libraries = libraryRepository.findByStatusOrderByIdentifier(true);
//        libraryWsDto.setSubLibraries(libraries);
//        libraryWsDto.setMediaList(new ArrayList<>());
//        List<Action> actionList = actionRepository.findByStatusOrderByIdentifier(true);
//        libraryWsDto.setActionList(actionList);
//        libraryWsDto.setActionsList(new ArrayList<>());
//        libraryWsDto.setSubLibrariesList(new ArrayList<>());
//        return libraryWsDto;
//    }
//
//    @PostMapping("/delete")
//    @ResponseBody
//    public LibraryWsDto deleteLibrary(@RequestBody LibraryWsDto libraryWsDto) {
//        try {
//            for (LibraryDto libraryDto : libraryWsDto.getLibraries()) {
//                libraryRepository.deleteByIdentifier(libraryDto.getIdentifier());
//            }
//        } catch (Exception e) {
//            //TODO check if this is needed
//            //TimeUnit.SECONDS.sleep(Long.valueOf(2));
//            return libraryWsDto;
//        }
//        libraryWsDto.setBaseUrl(ADMIN_LIBRARY);
//        libraryWsDto.setMessage("Data deleted successfully!!");
//        return libraryWsDto;
//    }
//
//    @PostMapping("/upload")
//    public LibraryWsDto uploadFile(@RequestBody MultipartFile file) {
//        LibraryWsDto libraryWsDto = new LibraryWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.LIBRARY, EntityConstants.LIBRARY, libraryWsDto);
//            if (StringUtils.isEmpty(libraryWsDto.getMessage())) {
//                libraryWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return libraryWsDto;
//    }
//
//    @PostMapping("/export")
//    @ResponseBody
//    public LibraryWsDto uploadFile(@RequestBody LibraryWsDto libraryWsDto) {
//
//        try {
//            libraryWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.LIBRARY, libraryWsDto.getHeaderFields()));
//            return libraryWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
//}
