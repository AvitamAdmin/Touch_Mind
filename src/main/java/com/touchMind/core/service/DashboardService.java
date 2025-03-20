package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.DashboardWsDto;
import com.touchMind.core.mongo.model.Dashboard;
import com.touchMind.form.DashboardForm;
import com.touchMind.web.controllers.QaSummaryData;

import java.util.List;

public interface DashboardService {
    Dashboard getDashboardByIdentifier(String id);

    List<Dashboard> getAllDashBoard();

    Dashboard getByNode(String node);

    DashboardForm edit(DashboardForm dashboardForm);

    QaSummaryData getDashBoard(Dashboard dashboard, String subsidiaryId, String days, String runner);

    DashboardWsDto handleEdit(DashboardWsDto request);

}
