package com.touchMind.fileimport.actions;

import com.touchMind.fileimport.service.impl.EntityField;

public interface FieldAction {
    Object performFieldAction(EntityField entityField);
}