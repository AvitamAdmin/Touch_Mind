package com.touchMind.fileimport.actions;

import com.touchMind.core.mongo.model.CommonFields;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.fileimport.service.impl.EntityField;
import com.touchMind.fileimport.strategies.FieldType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service(FieldType.ONE_TO_MANY_REF_FIELD)
public class OneToManyFieldActionAction implements FieldAction {

    public static final String TYPE = "Type";
    public static final String REGULAR_EXPRESSION = "\\s*,\\s*";
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Object performFieldAction(EntityField entityField) {
        Map<String, String> attributes = entityField.getAttributes();
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(attributes.get(TYPE));
        List<CommonFields> commonFieldsList = new ArrayList<>();
        if (genericImportRepository != null) {
            String field = attributes.get("refs");
            List<String> items = Arrays.asList(entityField.getValue().split(REGULAR_EXPRESSION));
            if ("Id".equalsIgnoreCase(field)) {
                items.stream().forEach(id -> {
                    if (StringUtils.isNotEmpty(id)) {
                        Optional<CommonFields> commonFieldsOptional = genericImportRepository.findById(new ObjectId(id));
                        if (commonFieldsOptional.isPresent()) {
                            commonFieldsList.add(commonFieldsOptional.get());
                        }
                    }
                });
            } else if ("identifier".equalsIgnoreCase(field)) {
                items.stream().forEach(id -> {
                    if (StringUtils.isNotEmpty(id)) {
                        commonFieldsList.add(genericImportRepository.findByIdentifier(id));
                    }
                });
            }
        }
        return commonFieldsList;
    }
}