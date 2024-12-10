package com.cheil.qa.validation;

import com.cheil.core.SpringContext;
import org.springframework.stereotype.Component;

@Component
public class AndConditionHandler extends ToolkitValidationHandler {

    @Override
    public void setNextValidationHandler(ToolkitValidationHandler toolkitValidationHandler) {
        this.toolkitValidationHandler = toolkitValidationHandler;
    }

    @Override
    public boolean validate(boolean previousValidationResult) throws Exception {
        boolean result = SpringContext.getBean(ParamValidatorService.class).validate(this.getContext(), this.getCurrentVariant(), this.getParamValue(), this.getCondition(), this.getApiValue());
        if ((previousValidationResult && result)) {
            if (toolkitValidationHandler != null) {
                return toolkitValidationHandler.validate(true);
            }
        } else {
            throw new Exception("Validation failed " + this.getCurrentVariant() + " is" + this.getParamValue() + " " + this.getCondition() + " " + this.getApiValue());
        }
        return result;
    }
}
