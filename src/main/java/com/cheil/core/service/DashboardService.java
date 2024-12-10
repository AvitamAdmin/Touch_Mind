package com.cheil.core.service;

import com.cheil.core.mongo.dto.DashboardWsDto;
import com.cheil.core.mongo.model.Dashboard;
import com.cheil.form.DashboardForm;
import com.cheil.web.controllers.QaSummaryData;

import java.util.List;

public interface DashboardService {
    Dashboard getDashboardByRecordId(String id);

    List<Dashboard> getAllDashBoard();

    Dashboard getByNode(String node);

    DashboardForm edit(DashboardForm dashboardForm);

    QaSummaryData getDashBoard(Dashboard dashboard, String subsidiaryId, String days, String runner);

    DashboardWsDto handleEdit(DashboardWsDto request);

}
