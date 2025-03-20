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

import java.util.Map;
import java.util.Optional;

@Service(FieldType.ONE_TO_ONE_REF_FIELD)
public class OneToOneFieldActionAction implements FieldAction {

    public static final String TYPE = "Type";
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Object performFieldAction(EntityField entityField) {
        Map<String, String> attributes = entityField.getAttributes();
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(attributes.get(TYPE));
        CommonFields commonFields = null;
        if (genericImportRepository != null) {
            String field = attributes.get("ref");
            if (StringUtils.isNotEmpty(entityField.getValue())) {
                if ("Id".equalsIgnoreCase(field)) {
                    Optional<CommonFields> commonFieldsOptional = genericImportRepository.findById(new ObjectId(entityField.getValue()));
                    if (commonFieldsOptional.isPresent()) {
                        commonFields = commonFieldsOptional.get();
                    }
                } else if ("recordId".equalsIgnoreCase(field)) {
                    commonFields = genericImportRepository.findByIdentifier(entityField.getValue());
                }
            }
        }
        return commonFields;
    }
}