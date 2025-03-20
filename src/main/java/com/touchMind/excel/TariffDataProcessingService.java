package com.touchMind.excel;

import com.touchMind.core.mongo.model.Tariff;
import com.touchMind.core.service.TariffService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TariffDataProcessingService {

    public static final String SIM_PLAN_MAPPING = "SIM_PLAN_MAPPING";
    public static final String SIM_PLAN_EXPORT = "SIM_PLAN_EXPORT";
    public static final String SIM_PLAN_OTP = "SIM_PLAN_OTP";
    Logger LOG = LoggerFactory.getLogger(TariffDataProcessingService.class);
    @Autowired
    private TariffService tariffService;

    public int processTariffData(Map<String, String> tariffData, String suitName) throws IOException, ParseException {
        processMappingFile(tariffData.get(SIM_PLAN_MAPPING), suitName);
        processMappingFile(tariffData.get(SIM_PLAN_EXPORT), suitName);
        processMappingFile(tariffData.get(SIM_PLAN_OTP), suitName);
        return 0;
    }

    public int processMappingFile(String filePath, String sessiomId) throws IOException, ParseException {
        if (StringUtils.isEmpty(filePath)) {
            return 1;
        }
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<Tariff> tariffs = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }

            Tariff t = new Tariff();
            if (filePath.contains(SIM_PLAN_MAPPING)) {
                processTariff(t, row, filePath, sessiomId);
                tariffService.saveTariff(t);
            }

            if (filePath.contains(SIM_PLAN_EXPORT)) {
                processTariff(t, row, filePath, sessiomId);
                List<Tariff> tarrifs = tariffService.findBySessionIdAndPlanId(sessiomId, t.getPlanId());
                for (Tariff tariff1 : tarrifs) {
                    tariff1.setTariffName(t.getTariffName());
                }
                if (tarrifs != null) {
                    tariffService.saveAllTariffs(tarrifs);
                }
            }

            if (filePath.contains(SIM_PLAN_OTP)) {
                processTariff(t, row, filePath, sessiomId);
                Tariff tariff = tariffService.findBySessionIdAndDeviceIdAndPlanId(sessiomId, t.getDeviceId(), t.getPlanId());
                if (tariff != null) {
                    tariff.setOtp(t.getOtp());
                    tariffService.saveTariff(tariff);
                }

            }
        }
        return 0;
    }

    public void processTariff(Tariff tariff, Row row, String filePath, String sessionId) throws ParseException {
        Iterator<Cell> cellIterator = row.cellIterator();

        tariff.setSessionId(sessionId);
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();

            switch (cell.getCellType()) {
                case NUMERIC:
                    if (columnIndex == 2 && filePath.contains(SIM_PLAN_OTP)) {
                        tariff.setOtp(cell.getNumericCellValue());
                    }
                    break;
                case STRING:
                    if (columnIndex == 0 && !filePath.contains(SIM_PLAN_EXPORT)) {
                        tariff.setDeviceId(cell.getStringCellValue());
                    }
                    if (columnIndex == 1) {
                        tariff.setPlanId(cell.getStringCellValue());
                    }
                    if (columnIndex == 7 && filePath.contains(SIM_PLAN_MAPPING)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss");
                        tariff.setUpdatedDate(formatter.parse(cell.getStringCellValue()));
                    }
                    if (columnIndex == 4 && filePath.contains(SIM_PLAN_EXPORT)) {
                        tariff.setTariffName(cell.getStringCellValue());
                    }
                case BOOLEAN:
                    if (columnIndex == 6 && filePath.contains(SIM_PLAN_MAPPING)) {
                        tariff.setActive(cell.getBooleanCellValue());
                    }
                    break;
            }

        }
    }

    public void processTariffCompareData(String filePath, String sessiomId) throws IOException {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<Tariff> tariffs = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() > 0) {
                Tariff tariff = getTariffForRow(row);
                tariff.setSessionId(sessiomId);
                tariffs.add(tariff);
                if (tariffs.size() > 50) {
                    tariffService.saveAllTariffs(tariffs);
                    tariffs = new ArrayList<>();
                }

            }
        }
        tariffService.saveAllTariffs(tariffs);
    }

    public Tariff getTariffForRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        Tariff tariff = new Tariff();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            try {
                switch (cell.getColumnIndex()) {
                    case 0:
                        tariff.setDeviceId(cell.getStringCellValue());
                        break;
                    case 1:
                        tariff.setPlanId(cell.getStringCellValue());
                        break;
                    case 2:
                        tariff.setTariffName(cell.getStringCellValue());
                        break;
                    case 3:
                        tariff.setOtp(Double.valueOf(Util.getValue(cell).getValue()));
                        break;
                }
            } catch (Exception e) {
                LOG.error("Row NO:" + row.getRowNum() + " => Error occurred processing the Cell, missing or empty value" + tariff);
            }

        }
        LOG.info("Tariff object conversion was succss: " + tariff);
        return tariff;
    }
}
