package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ModelWsDto;
import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.model.Model;

import java.util.List;

public interface ModelService {


    String getVariantAsCommaSeparatedList(ReportDto reportDto);

    String getVariantAsCommaSeparatedListForModelIds(List<String> models);

    List<String> getVariantsListForModels(List<String> models);

    List<String> getVariantListForCommaSeparatedSkus(ReportDto reportDto);

    List<Model> findAll();

    List<Model> findBySubsidiaryId(String id);

    ModelWsDto handleEdit(ModelWsDto request);
}
