package com.touchMind.web.controllers.admin.media;

import com.touchMind.core.mongo.dto.MediaDto;
import com.touchMind.core.mongo.dto.MediaWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Media;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.MediaRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.MediaService;
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
@RequestMapping("/admin/media")
public class MediaController extends BaseController {
    public static final String ADMIN_MEDIA = "/admin/media";

    public static final String IMPEX_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
            + "resources" + File.separator + "static" + File.separator + "impex" + File.separator;
    Logger logger = LoggerFactory.getLogger(MediaController.class);
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public MediaWsDto getAllModels(@RequestBody MediaWsDto mediaWsDto) {
        Pageable pageable = getPageable(mediaWsDto.getPage(), mediaWsDto.getSizePerPage(), mediaWsDto.getSortDirection(), mediaWsDto.getSortField());
        MediaDto mediaDto = CollectionUtils.isNotEmpty(mediaWsDto.getMedias()) ? mediaWsDto.getMedias().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(mediaDto, mediaWsDto.getOperator());
        Media media = mediaDto != null ? modelMapper.map(mediaDto, Media.class) : null;
        Page<Media> page = isSearchActive(media) != null ? mediaRepository.findAll(Example.of(media, exampleMatcher), pageable) : mediaRepository.findAll(pageable);
        Type listType = new TypeToken<List<MediaDto>>() {
        }.getType();
        mediaWsDto.setMedias(modelMapper.map(page.getContent(), listType));
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        mediaWsDto.setTotalPages(page.getTotalPages());
        mediaWsDto.setTotalRecords(page.getTotalElements());
        mediaWsDto.setAttributeList(getConfiguredAttributes(mediaWsDto.getNode()));
        mediaWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.MEDIA));
        return mediaWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody MediaWsDto mediaWsDto) {
        return getConfiguredAttributes(mediaWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Media());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.MEDIA);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody MediaDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(mediaRepository.findByIdentifier(recordId), MediaDto.class);
    }

    @GetMapping("/get")
    public MediaWsDto getActiveMedia() {
        MediaWsDto mediaWsDto = new MediaWsDto();
        Type listType = new TypeToken<List<MediaDto>>() {
        }.getType();
        mediaWsDto.setMedias(modelMapper.map(mediaRepository.findByStatusOrderByIdentifier(true), listType));
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        return mediaWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public MediaWsDto handleEdit(@RequestBody MultipartFile file) throws IOException {
        return mediaService.handleEdit(file);
    }

    @GetMapping("/add")
    @ResponseBody
    public MediaWsDto addInterface() {
        MediaWsDto mediaWsDto = new MediaWsDto();
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        return mediaWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public MediaWsDto edits(@RequestBody MediaWsDto request) {
        MediaWsDto mediaWsDto = new MediaWsDto();
        List<Media> mediaList = new ArrayList<>();
        for (MediaDto mediaDto : request.getMedias()) {
            mediaList.add(mediaRepository.findByIdentifier(mediaDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<MediaDto>>() {
        }.getType();
        mediaWsDto.setMedias(modelMapper.map(mediaList, listType));
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        return mediaWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public MediaWsDto deleteInterface(@RequestBody MediaWsDto mediaWsDto) {
        for (MediaDto mediaDto : mediaWsDto.getMedias()) {
            mediaRepository.deleteByIdentifier(mediaDto.getIdentifier());
        }
        mediaWsDto.setMessage("Data deleted successfully!!");
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        return mediaWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public MediaWsDto uploadFile(@RequestBody MultipartFile file) {
        MediaWsDto mediaWsDto = new MediaWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.MEDIA, EntityConstants.MEDIA, mediaWsDto);
            if (StringUtils.isEmpty(mediaWsDto.getMessage())) {
                mediaWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return mediaWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public MediaWsDto uploadFile(@RequestBody MediaWsDto mediaWsDto) {

        try {
            mediaWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MEDIA, mediaWsDto.getHeaderFields()));
            return mediaWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
