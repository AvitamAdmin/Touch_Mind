import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ShortCutGenerator {
    public static void main(String[] args) throws IOException {
        FileInputStream file = new FileInputStream(new File("Models.xlsx"));

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook

        XSSFSheet sheet = workbook.getSheetAt(0);
        System.out.println(sheet.getSheetName());
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();

        Map<String, Map<String, Object>> shortcuts = new HashMap<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            String zero = null;
            if (row.getCell(0) != null) {
                zero = row.getCell(0).getStringCellValue();
            }
            String one = null;
            if (row.getCell(1) != null) {
                one = row.getCell(1).getStringCellValue();
            }
            String three = null;
            if (row.getCell(3) != null) {
                three = row.getCell(3).getStringCellValue();
            }
            Map<String, Object> variant = null;
            String[] oo = one.split(":");
            if (shortcuts.containsKey(oo[0])) {
                variant = shortcuts.get(oo[0]);
                List<String> variantModels = (List<String>) variant.get("variants");
                variantModels.add(zero);
                variant.put("variants", variantModels);
                shortcuts.put(oo[0], variant);
            } else {
                variant = new HashMap<>();
                List<String> variantModels = new ArrayList<>();
                variantModels.add(zero);
                variant.put("Description", three);
                variant.put("variants", variantModels);
                shortcuts.put(oo[0], variant);
            }

        }
        int i = 1;
        int j = 1;
        for (String k : shortcuts.keySet()) {
            Map<String, Object> data = shortcuts.get(k);
            System.out.println("INSERT INTO model (id,status,model_id,short_description,subsidiary_id) VALUES (" + i + ",true,'" + k + "','" + data.get("Description") + "','1');");
            List<String> variantModels = (List<String>) data.get("variants");
            for (String variant : variantModels) {
                System.out.println("INSERT INTO variant (variant_id,model_id,status) VALUES ('" + variant + "'," + i + ",true);");
                j++;
            }
            i++;
        }
        System.out.println("Total variants :" + j);
    }
}
