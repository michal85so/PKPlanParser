package pl.pk.policht.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileConnector {

    private String path;

    public FileConnector(String path) {
        this.path = path;
    }

    public Sheet connectAndGetSheet() {
        try (BufferedInputStream excelFile = new BufferedInputStream(new FileInputStream(new File(path)))) {
            HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
            return workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
