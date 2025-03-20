package com.touchMind.web.controllers.admin.savedquery;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.repository.SavedQueryRepository;
import com.touchMind.web.controllers.BaseController;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/savedquery")
public class SavedQueryController extends BaseController {

    Logger logger = LoggerFactory.getLogger(SavedQueryController.class);
    @Autowired
    private SavedQueryRepository savedQueryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/delete")
    @ResponseBody
    public String deleteQuery(@RequestBody SavedQueryDto savedQueryDto) {
        savedQueryRepository.deleteByIdentifier(savedQueryDto.getIdentifier());
        return "Success";
    }
}
