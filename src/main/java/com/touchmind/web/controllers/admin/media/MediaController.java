package com.touchmind.web.controllers.admin.media;

import com.touchmind.core.mongo.dto.MediaDto;
import com.touchmind.core.mongo.dto.MediaWsDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.Media;
import com.touchmind.core.mongo.repository.CategoryRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.MediaRepository;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.MediaService;
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

    @PostMapping
    @ResponseBody
    public MediaWsDto getAllModels(@RequestBody MediaWsDto mediaWsDto) {
        Pageable pageable = getPageable(mediaWsDto.getPage(), mediaWsDto.getSizePerPage(), mediaWsDto.getSortDirection(), mediaWsDto.getSortField());
        MediaDto mediaDto = CollectionUtils.isNotEmpty(mediaWsDto.getMedias()) ? mediaWsDto.getMedias().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(mediaDto, mediaWsDto.getOperator());
        Media media = mediaDto != null ? modelMapper.map(mediaDto, Media.class) : null;
        Page<Media> page = isSearchActive(media) != null ? mediaRepository.findAll(Example.of(media, exampleMatcher), pageable) : mediaRepository.findAll(pageable);
        mediaWsDto.setMedias(modelMapper.map(page.getContent(), List.class));
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        mediaWsDto.setTotalPages(page.getTotalPages());
        mediaWsDto.setTotalRecords(page.getTotalElements());
        mediaWsDto.setAttributeList(getConfiguredAttributes(mediaWsDto.getNode()));
        return mediaWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Media());
    }


    @GetMapping("/get")
    public MediaWsDto getActiveMedia() {
        MediaWsDto mediaWsDto = new MediaWsDto();
        mediaWsDto.setMedias(modelMapper.map(mediaRepository.findByStatusOrderByIdentifier(true), List.class));
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
            mediaList.add(mediaRepository.findByRecordId(mediaDto.getRecordId()));
        }
        mediaWsDto.setMedias(modelMapper.map(mediaList, List.class));
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        return mediaWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public MediaWsDto deleteInterface(@RequestBody MediaWsDto mediaWsDto) {
        for (MediaDto mediaDto : mediaWsDto.getMedias()) {
            mediaRepository.deleteByRecordId(mediaDto.getRecordId());
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

    @GetMapping("/export")
    @ResponseBody
    public MediaWsDto uploadFile() {
        MediaWsDto mediaWsDto = new MediaWsDto();
        try {
            mediaWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MEDIA));
            return mediaWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
