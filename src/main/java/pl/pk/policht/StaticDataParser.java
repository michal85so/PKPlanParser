package pl.pk.policht;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.domain.Date;
import pl.pk.policht.domain.Group;
import pl.pk.policht.domain.Hour;
import pl.pk.policht.domain.Lecture;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class StaticDataParser {
    private Sheet sheet;
    private List<CellRangeAddress> mergedRegions;
    private List<Date> dates = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private Map<Date, List<Hour>> hours = new HashMap<>();
    private List<Lecture> lectures = new ArrayList<>();

    public StaticDataParser(Sheet sheet) {
        this.sheet = sheet;
        mergedRegions = sheet.getMergedRegions();
    }

    public void parse() {
        parseDates();
        parseGroups();
        parseHours();
        parseLectures();
        showResults();
    }

    private void showResults() {
        dates.stream().forEach(System.out::println);
        groups.stream().forEach(System.out::println);
        lectures.stream().forEach(System.out::println);
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

            if ("sobota".equalsIgnoreCase(cellValue) || "niedziela".equalsIgnoreCase(cellValue))
                continue;

            final String value = cellValue;
            Optional<CellRangeAddress> cellMergedRegion = checkIsInMergedColumnRegions(row.getRowNum(), cell.getColumnIndex());
            cellMergedRegion.ifPresent(mergedRegion -> groups.add(new Group(value, mergedRegion.getFirstColumn(), mergedRegion.getLastColumn())));
            if (!cellMergedRegion.isPresent())
                groups.add(new Group(value, cell.getColumnIndex(), cell.getColumnIndex()));
        }
    }

    private void parseHours() {
        for (Date date : dates) {
            hours.put(date, new ArrayList<>());
            for (int i = date.getFirstRow(); i <= date.getLastRow(); i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(1);
                hours.get(date).add(new Hour(cell.getStringCellValue(), i));
//                date.getHours().add(new Hour(cell.getStringCellValue(), i));
            }
        }
    }

    private void parseLectures() {
        for (Date date : dates) {
            for (Group group : groups) {
                for (int i = group.getFirstCol(); i <= group.getLastCol(); i++) {
                    for (int j = date.getFirstRow(); j <= date.getLastRow(); j++) {
                        Row row = sheet.getRow(j);
                        Cell cell = row.getCell(i);
                        if (cell == null)
                            continue;
                        String value = cell.getStringCellValue();
                        if (value != null && !value.isEmpty()) {
                            Optional<CellRangeAddress> mergedRegion = checkIsInMergedColumnRegions(j, i);
                            if (mergedRegion.isPresent()) {
                                Lecture lecture = new Lecture(value);
                                CellRangeAddress region = mergedRegion.get();
                                for (int k = region.getFirstRow(); k <= region.getLastRow(); k++) {
                                    Hour hour = hours.get(date).get(k - j);
                                    lecture.getHours().add(hour);
                                }
                                for (int k = region.getFirstColumn(); k <= region.getLastColumn(); k++) {
                                    for (Group innerGroup : groups) {
                                        if (k >= innerGroup.getFirstCol() && k <= innerGroup.getLastCol())
                                            lecture.getGroups().add(innerGroup);
                                    }
                                }
                                lectures.add(lecture);
                            }
                            else {

                            }
                        }
                    }
                }
            }
        }
    }
}
