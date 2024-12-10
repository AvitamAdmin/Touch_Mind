package com.cheil.qa.validation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

@Getter
@Setter
@NoArgsConstructor
public abstract class ToolkitValidationHandler implements ToolkitValidator {

    protected ToolkitValidationHandler toolkitValidationHandler;
    Logger logger = LoggerFactory.getLogger(ToolkitValidationHandler.class);
    private ITestContext context;
    private String currentVariant;
    private String paramValue;
    private String condition;
    private String apiValue;

    public abstract void setNextValidationHandler(ToolkitValidationHandler toolkitValidationHandler);

    public boolean validate(boolean previousValidationResult) throws Exception {
        if (this.toolkitValidationHandler != null) {
            return this.toolkitValidationHandler.validate(previousValidationResult);
        } else {
            logger.warn("Validation not possible !");
        }
        return true;
    }

}
