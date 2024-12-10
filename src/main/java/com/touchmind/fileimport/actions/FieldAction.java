package com.touchmind.fileimport.actions;

import com.touchmind.fileimport.service.impl.EntityField;

public interface FieldAction {
    Object performFieldAction(EntityField entityField);
}