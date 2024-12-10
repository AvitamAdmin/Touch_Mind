package com.touchmind.excel;

import com.touchmind.core.mongo.model.Tariff;
import com.touchmind.core.service.TariffService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

@Service
public class ExcelFileCompareService {

    @Autowired
    private TariffDataProcessingService tariffDataProcessingService;
    @Autowired
    private TariffService tariffService;

    //@Autowired
    //private Report report;

    public void compareTwoExcelFiles(String filePathFirst, String filePathTwo, String fileNameOne, String fileNameTwo) throws IOException {
        if (StringUtils.isEmpty(filePathFirst) || StringUtils.isEmpty(filePathTwo)) {
            return;
        }
        String suitName = UUID.randomUUID().toString();
        tariffDataProcessingService.processTariffCompareData(filePathFirst, suitName);
        XSSFWorkbook workbookTwo = new XSSFWorkbook(filePathTwo);
        XSSFSheet sheetTwo = workbookTwo.getSheetAt(0);
        Iterator<Row> rowIteratorTwo = sheetTwo.iterator();

        //report.reportStart(fileNameOne + " < = > " + fileNameTwo);
        boolean allRowsMatch = true;
        while (rowIteratorTwo.hasNext()) {
            Row row = rowIteratorTwo.next();
            if (row.getRowNum() == 0) {
                continue;
            }
            Tariff tariffTwo = tariffDataProcessingService.getTariffForRow(row);
            if (StringUtils.isNotEmpty(tariffTwo.getDeviceId()) && StringUtils.isNotEmpty(tariffTwo.getPlanId())) {
                Tariff tariffOne = tariffService.findBySessionIdAndDeviceIdAndPlanId(suitName, tariffTwo.getDeviceId(), tariffTwo.getPlanId());
                if (tariffOne != null) {
                    /*
                    boolean result = compareRow(tariffOne, tariffTwo, row.getRowNum(), report);
                    if (!result) {
                        allRowsMatch = result;
                    }

                     */
                } else {
                    //report.reportFailed("FILE", fileNameOne + " Missing diviceId and planId ");
                }
            } else {
                // report.reportFailed("Report", "rowIteratorTwo" + " is empty nothing to compare!!");
            }
        }
        if (allRowsMatch) {
            //report.reportPassed("Result", "Data in both files is same.");
        }
        //report.reportFlush();
    }
/*
    private boolean compareRow(Tariff tariffOne, Tariff tariffTwo, int rowNo, Report report) {
        boolean tariffIsEqual = StringUtils.isNotEmpty(tariffOne.getTariffName()) && tariffOne.getTariffName().equals(tariffTwo.getTariffName());
        boolean otpIsEqual = false;
        if (null != tariffOne.getOtp() && null != tariffTwo.getOtp()) {
            otpIsEqual = Double.compare(tariffOne.getOtp(), tariffTwo.getOtp()) == 0;
        }
        boolean result = tariffIsEqual && otpIsEqual;
        if (!tariffIsEqual) {
            //report.reportFailed("Row:" + rowNo + "  Cell:TariffName", tariffOne.getDeviceId() + " : " + tariffOne.getPlanId() + " => " + tariffOne.getTariffName() + " NOT EQUAL " + tariffTwo.getTariffName());
        }
        if (!otpIsEqual) {
            //report.reportFailed("Row:" + rowNo + "  Cell:OTP", tariffOne.getDeviceId() + " : " + tariffOne.getPlanId() + " => " + tariffOne.getOtp() + " NOT EQUAL " + tariffTwo.getOtp());
        }
        return result;
    }

 */
}
