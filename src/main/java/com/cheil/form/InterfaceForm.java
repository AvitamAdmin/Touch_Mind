package com.cheil.form;

import com.cheil.core.mongo.model.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InterfaceForm extends BaseForm {
    private String path;
    private Node parentNode;
    private Integer displayPriority;
    private List<InterfaceForm> interfaceFormList;
}
