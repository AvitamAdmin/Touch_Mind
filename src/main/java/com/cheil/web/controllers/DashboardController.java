package com.cheil.web.controllers;

import com.cheil.core.mongo.dto.DashboardWsDto;
import com.cheil.core.mongo.model.Dashboard;
import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.service.DashboardService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    public static final String DASHBOARD = "/dashboard/";
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private NodeRepository nodeRepository;


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DashboardWsDto qaSummary(@PathVariable(required = true) String id, Model model, @RequestParam(required = false) String subsidiaryId, @RequestParam(defaultValue = "0") String days, @RequestParam(required = false) String runner) {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        Node node = nodeRepository.findByPath(DASHBOARD + id);
        Dashboard dashboard = dashboardService.getByNode(String.valueOf(node.getId()));
        QaSummaryData qaSummaryData = dashboardService.getDashBoard(dashboard, subsidiaryId, days, runner);
        if (CollectionUtils.isNotEmpty(qaSummaryData.getSubsidiaryData())) {
            List<Subsidiary> subsidiaries = new ArrayList<>();
            for (DashboardTableData subData : qaSummaryData.getSubsidiaryData()) {
                if (StringUtils.isNotEmpty(subData.getSubsidiary())) {
                    subsidiaries.add(subsidiaryRepository.findByIdentifier(subData.getSubsidiary()));
                }
            }
            dashboardWsDto.setSubsidiaries(subsidiaries);
        }
        // TODO
        /*dashboardWsDto.setDashboard(dashboard);
        dashboardWsDto.setDashboardPath(node.getPath());
        dashboardWsDto.setQaSummaryData(qaSummaryData);
        if (StringUtils.isNotEmpty(subsidiaryId)) {
            List<String> subIdList = new ArrayList<>();

            String[] subIds = subsidiaryId.split(",");
            for (String subId : subIds) {
                //TODO Check if this is correctly fetching the data
                subIdList.add(subsidiaryRepository.findByRecordId(Long.valueOf(subId)).getIdentifier());
            }
            dashboardWsDto.setSubIdList(subIdList);
        }
        dashboardWsDto.setDays(days);
        if (StringUtils.isNotEmpty(runner)) {
            dashboardWsDto.setRunner(Arrays.stream(runner.split(",")).collect(Collectors.toList()));
        }*/
        return dashboardWsDto;
    }
}
