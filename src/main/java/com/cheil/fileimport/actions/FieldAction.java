package com.cheil.fileimport.actions;

import com.cheil.fileimport.service.impl.EntityField;

public interface FieldAction {
    Object performFieldAction(EntityField entityField);
}