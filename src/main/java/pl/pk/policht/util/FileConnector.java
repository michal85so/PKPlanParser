package pl.pk.policht.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;

public class FileConnector {

    public Sheet connectAndGetSheet() {
        try (BufferedInputStream excelFile = new BufferedInputStream(new FileInputStream(new File("plan.xls")))) {
            HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
            return workbook.getSheetAt(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
