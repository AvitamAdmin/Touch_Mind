package com.touchmind.web.controllers.admin.savedquery;

import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.repository.SavedQueryRepository;
import com.touchmind.web.controllers.BaseController;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        savedQueryRepository.deleteByRecordId(savedQueryDto.getRecordId());
        return "Success";
    }
}
