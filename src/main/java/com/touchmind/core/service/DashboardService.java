package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.DashboardWsDto;
import com.touchmind.core.mongo.model.Dashboard;
import com.touchmind.form.DashboardForm;
import com.touchmind.web.controllers.QaSummaryData;

import java.util.List;

public interface DashboardService {
    Dashboard getDashboardByRecordId(String id);

    List<Dashboard> getAllDashBoard();

    Dashboard getByNode(String node);

    DashboardForm edit(DashboardForm dashboardForm);

    QaSummaryData getDashBoard(Dashboard dashboard, String subsidiaryId, String days, String runner);

    DashboardWsDto handleEdit(DashboardWsDto request);

}
