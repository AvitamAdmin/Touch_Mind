package com.touchmind.excel;

import org.apache.poi.ss.usermodel.Cell;

public class Util {

    public static ObjectWapper getValue(Cell cell) {
        ObjectWapper object = new ObjectWapper();
        switch (cell.getCellType()) {
            case NUMERIC:
                object.setDoubleValue(cell.getNumericCellValue());
                break;
            case STRING:
                object.setStringValue(cell.getStringCellValue());
                break;
            case BOOLEAN:
                object.setBooleanValue(cell.getBooleanCellValue());
                break;
        }
        return object;
    }

}
