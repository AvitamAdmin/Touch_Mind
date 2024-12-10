package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ModelWsDto;
import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.model.Model;

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
