package pl.pk.policht;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.domain.DateRowRange;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaticDataParser {
    private Sheet sheet;
    private List<CellRangeAddress> mergedRegions;
    private List<DateRowRange> dates = new ArrayList<>();

    public StaticDataParser(Sheet sheet) {
        this.sheet = sheet;
        mergedRegions = sheet.getMergedRegions();
    }

    public void parse() {
        parseDates();
    }

    private void showResults() {
        dates.stream().forEach(System.out::println);
    }

    private void parseDates() {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(0);
            if (cell != null && CellType.NUMERIC.getCode() == cell.getCellType()) {
                LocalDate localDate = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Optional<CellRangeAddress> rangeAddress = mergedRegions.parallelStream()
                        .filter(region ->
                                0 == region.getFirstColumn()
                                        && row.getRowNum() >= region.getFirstRow()
                                        && row.getRowNum() <= region.getLastRow())
                        .findAny();
                rangeAddress.ifPresent(address -> dates.add(new DateRowRange(localDate, address.getFirstRow(), address.getLastRow())));
            }

        }
    }
}
