package com.touchMind.web.controllers.admin.domtree;

import com.touchMind.core.mongo.dto.CrawlerPathDto;
import com.touchMind.core.mongo.dto.CrawlerPathWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.CrawlerPath;
import com.touchMind.core.mongo.repository.CrawlerPathRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.CrawlerPathService;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/dompath")
public class DomPathController extends BaseController {

    public static final String ADMIN_DOM_PATH = "/admin/dompath";

    Logger logger = LoggerFactory.getLogger(DomPathController.class);

    @Autowired
    private CrawlerPathRepository crawlerPathRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CrawlerPathService crawlerPathService;

    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public CrawlerPathWsDto getAllPaths(@RequestBody CrawlerPathWsDto crawlerPathWsDto) {
        Pageable pageable = getPageable(crawlerPathWsDto.getPage(), crawlerPathWsDto.getSizePerPage(), crawlerPathWsDto.getSortDirection(), crawlerPathWsDto.getSortField());
        CrawlerPathDto crawlerPathDto = CollectionUtils.isNotEmpty(crawlerPathWsDto.getListPaths()) ? crawlerPathWsDto.getListPaths().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(crawlerPathDto, crawlerPathWsDto.getOperator());
        CrawlerPath crawlerPath = crawlerPathDto != null ? modelMapper.map(crawlerPathDto, CrawlerPath.class) : null;
        Page<CrawlerPath> page = isSearchActive(crawlerPath) != null ? crawlerPathRepository.findAll(Example.of(crawlerPath, exampleMatcher), pageable) : crawlerPathRepository.findAll(pageable);
        Type listType = new TypeToken<List<CrawlerPathDto>>() {
        }.getType();
        crawlerPathWsDto.setListPaths(modelMapper.map(page.getContent(), listType));
        crawlerPathWsDto.setBaseUrl(ADMIN_DOM_PATH);
        crawlerPathWsDto.setTotalPages(page.getTotalPages());
        crawlerPathWsDto.setTotalRecords(page.getTotalElements());
        crawlerPathWsDto.setAttributeList(getConfiguredAttributes(crawlerPathWsDto.getNode()));
        return crawlerPathWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody CrawlerPathWsDto crawlerPathWsDto) {
        return getConfiguredAttributes(crawlerPathWsDto.getNode());
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new CrawlerPath());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.CRAWLER_PATH);
    }

    @GetMapping("/get")
    @ResponseBody
    public CrawlerPathWsDto getActiveTrees() {
        CrawlerPathWsDto crawlerPathWsDto = new CrawlerPathWsDto();
        crawlerPathWsDto.setBaseUrl(ADMIN_DOM_PATH);
        Type listType = new TypeToken<List<CrawlerPathDto>>() {
        }.getType();
        crawlerPathWsDto.setListPaths(modelMapper.map(crawlerPathRepository.findByStatusOrderByIdentifier(true), listType));
        return crawlerPathWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CrawlerPathDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(crawlerPathRepository.findByIdentifier(recordId), CrawlerPathDto.class);
    }

    @GetMapping("/edit")
    @ResponseBody
    public CrawlerPathWsDto editDomPath(@RequestBody CrawlerPathWsDto crawlerPathWsDto) {
        List<CrawlerPathDto> updatedCrawlerPathList = new ArrayList<>();

        for (CrawlerPathDto crawlerPathDto : crawlerPathWsDto.getListPaths()) {
            CrawlerPath crawlerPath = crawlerPathRepository.findByIdentifier(crawlerPathDto.getIdentifier());

            if (crawlerPath != null) {
                CrawlerPathDto mappedDto = modelMapper.map(crawlerPath, CrawlerPathDto.class);
                updatedCrawlerPathList.add(mappedDto);
            } else {
                updatedCrawlerPathList.add(crawlerPathDto);
            }
        }
        crawlerPathWsDto.setListPaths(updatedCrawlerPathList);

        return crawlerPathWsDto;
    }


    @PostMapping("/edit")
    public CrawlerPathWsDto handleEdit(@RequestBody CrawlerPathWsDto crawlerPathWsDto) {
        return crawlerPathService.handleEdit(crawlerPathWsDto);
    }

//    private void populateCommonData(CrawlerPathForm crawlerPathForm, Model model) {
//        model.addAttribute("editForm", crawlerPathForm);
//        model.addAttribute("subsidiaries", subsidiaryRepository.findByStatusOrderBySubId(true));
//        model.addAttribute("sites", siteRepository.findByStatusOrderBySiteId(true));
//    }

    @PostMapping("/delete")
    @ResponseBody
    public CrawlerPathWsDto deletePath(@RequestBody CrawlerPathWsDto crawlerPathWsDto) {
        for (CrawlerPathDto crawlerPathDto : crawlerPathWsDto.getListPaths()) {
            crawlerPathRepository.deleteByIdentifier(crawlerPathDto.getIdentifier());
        }
        crawlerPathWsDto.setMessage("Data deleted successfully");
        return crawlerPathWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public CrawlerPathWsDto addDomPath() {
        CrawlerPathWsDto crawlerPathWsDto = new CrawlerPathWsDto();
        crawlerPathWsDto.setBaseUrl(ADMIN_DOM_PATH);
        Type listType = new TypeToken<List<CrawlerPathDto>>() {
        }.getType();
        crawlerPathWsDto.setListPaths(modelMapper.map(crawlerPathRepository.findByStatusOrderByIdentifier(true), listType));
        return crawlerPathWsDto;
    }


}
