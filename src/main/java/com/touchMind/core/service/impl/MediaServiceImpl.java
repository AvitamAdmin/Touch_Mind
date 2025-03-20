package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.MediaDto;
import com.touchMind.core.mongo.dto.MediaWsDto;
import com.touchMind.core.mongo.model.Media;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.MediaRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.MediaService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {

    public static final String ADMIN_MEDIA = "/admin/media";
    public static final String IMPEX_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
            + "resources" + File.separator + "static" + File.separator + "impex" + File.separator;
    private final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Environment env;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public MediaWsDto handleEdit(MultipartFile request) {
        MediaWsDto mediaWsDto = new MediaWsDto();
        Media requestData = new Media();
        String fileName = request.getOriginalFilename();
        Path path = Paths.get(IMPEX_PATH + fileName);
        String serverUrl = env.getProperty("server.url");
        requestData.setFileName(serverUrl + File.separator + "impex" + File.separator + fileName);
        try {
            Files.write(path, request.getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        requestData.setIdentifier(fileName);
        baseService.populateCommonData(requestData);
        mediaRepository.save(requestData);
        mediaWsDto.setMessage("Media updated successfully!");
        mediaWsDto.setBaseUrl(ADMIN_MEDIA);
        mediaWsDto.setMedias(List.of(modelMapper.map(requestData, MediaDto.class)));
        return mediaWsDto;
    }
}
