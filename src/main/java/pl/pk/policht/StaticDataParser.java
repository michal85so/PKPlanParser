package pl.pk.policht;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.domain.Date;
import pl.pk.policht.domain.Group;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaticDataParser {
    private Sheet sheet;
    private List<CellRangeAddress> mergedRegions;
    private List<Date> dates = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    public StaticDataParser(Sheet sheet) {
        this.sheet = sheet;
        mergedRegions = sheet.getMergedRegions();
    }

    public void parse() {
        parseDates();
        parseGroups();
        showResults();
    }

    private void showResults() {
        dates.stream().forEach(System.out::println);
        groups.stream().forEach(System.out::println);
    }

    private void parseDates() {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(0);
            if (cell != null && CellType.NUMERIC.getCode() == cell.getCellType()) {
                LocalDate localDate = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Optional<CellRangeAddress> rangeAddress = checkIsInMergedRowRegions(row.getRowNum());
                rangeAddress.ifPresent(address -> dates.add(new Date(localDate, address.getFirstRow(), address.getLastRow())));
            }

        }
    }

    private Optional<CellRangeAddress> checkIsInMergedRowRegions(int row) {
        return mergedRegions.parallelStream()
                .filter(region ->
                        0 == region.getFirstColumn()
                                && row >= region.getFirstRow()
                                && row <= region.getLastRow())
                .findAny();
    }

    private Optional<CellRangeAddress> checkIsInMergedColumnRegions(int row, int column) {
        return mergedRegions.parallelStream()
                .filter(region -> row == region.getFirstRow()
                        && column >= region.getFirstColumn()
                        && column <= region.getLastColumn())
                .findAny();
    }

    private void parseGroups() {
        int groupRow = dates.get(0).getFirstRow() - 1;
        Row row = sheet.getRow(groupRow);
        int emptyColumnCounter = 0;
        for (int i = 0;;i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                emptyColumnCounter++;
                if (emptyColumnCounter == 5)
                    break;
                continue;
            }
            emptyColumnCounter = 0;

            String cellValue = null;
            if (CellType.STRING.getCode() == cell.getCellType())
                cellValue = cell.getStringCellValue();
            else if (CellType.NUMERIC.getCode() == cell.getCellType())
                cellValue = String.valueOf(cell.getNumericCellValue());
            else
                continue;

            final String value = cellValue;
            Optional<CellRangeAddress> cellMergedRegion = checkIsInMergedColumnRegions(row.getRowNum(), cell.getColumnIndex());
            cellMergedRegion.ifPresent(mergedRegion -> groups.add(new Group(value, mergedRegion.getFirstColumn(), mergedRegion.getLastColumn())));
            if (!cellMergedRegion.isPresent())
                groups.add(new Group(value, cell.getColumnIndex(), cell.getColumnIndex()));
        }
    }
}
