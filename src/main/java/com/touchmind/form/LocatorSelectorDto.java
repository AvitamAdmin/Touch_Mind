package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocatorSelectorDto extends BaseForm {
    private String xpathSelector;
    private String cssSelector;
    private String idSelector;
    private String othersSelector;
    private String inputData;
    private String errorMsg;

    @Override
    public String toString() {
        return "xpathSelector=" + xpathSelector + " , cssSelector=" + cssSelector +
                " , idSelector=" + idSelector + " , othersSelector=" + othersSelector + " , inputData=" + inputData;
    }

}
