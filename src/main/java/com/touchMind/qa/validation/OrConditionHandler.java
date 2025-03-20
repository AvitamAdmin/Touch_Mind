package com.touchMind.qa.validation;

import com.touchMind.core.SpringContext;
import org.springframework.stereotype.Component;

@Component
public class OrConditionHandler extends ToolkitValidationHandler {

    @Override
    public void setNextValidationHandler(ToolkitValidationHandler toolkitValidationHandler) {
        this.toolkitValidationHandler = toolkitValidationHandler;
    }

    @Override
    public boolean validate(boolean previousValidationResult) throws Exception {
        boolean result = SpringContext.getBean(ParamValidatorService.class).validate(this.getContext(), this.getCurrentVariant(), this.getParamValue(), this.getCondition(), this.getApiValue());
        if ((previousValidationResult || result)) {
            if (toolkitValidationHandler != null) {
                return toolkitValidationHandler.validate(true);
            }
        } else {
            throw new Exception("Validation failed " + toolkitValidationHandler.getCurrentVariant() + " " + toolkitValidationHandler.getParamValue() + " " + toolkitValidationHandler.getCondition() + " " + toolkitValidationHandler.getApiValue());
        }
        return result;
    }
}
