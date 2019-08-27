package pl.pk.policht.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.dao.LectureNameDao;
import pl.pk.policht.dao.LectureTypeDao;
import pl.pk.policht.dao.LecturerDao;
import pl.pk.policht.domain.ClassRoom;
import pl.pk.policht.domain.Group;
import pl.pk.policht.domain.Lecture;
import pl.pk.policht.domain.Lecturer;

import java.util.*;

public abstract class AbstractDataParser {
    protected static final int DATE_COLUMN_POSITION = 0;
    Sheet sheet;
    List<CellRangeAddress> mergedRegions;
    LecturerDao lecturerDao;
    LectureTypeDao lectureTypeDao;
    LectureNameDao lectureNameDao;
    List<Group> groups = new ArrayList<>();
    List<Lecture> lectures = new ArrayList<>();
    Map<String, ClassRoom> classRooms = new HashMap<>();
    Map<String, Lecturer> lecturers = new HashMap<>();
    Map<String, Group> lectureGroups = new HashMap<>();

    public AbstractDataParser(Sheet sheet, LecturerDao lecturerDao, LectureTypeDao lectureTypeDao, LectureNameDao lectureNameDao) {
        this.sheet = sheet;
        this.mergedRegions = sheet.getMergedRegions();
        this.lecturerDao = lecturerDao;
        this.lectureTypeDao = lectureTypeDao;
        this.lectureNameDao = lectureNameDao;
    }

    Optional<CellRangeAddress> checkDateIsInMergedRowRegions(int row) {
        return mergedRegions.parallelStream()
                .filter(region ->
                        DATE_COLUMN_POSITION == region.getFirstColumn()
                                && row >= region.getFirstRow()
                                && row <= region.getLastRow())
                .findAny();
    }

    Optional<CellRangeAddress> checkIsInMergedColumnRegions(int row, int column) {
        return mergedRegions.parallelStream()
                .filter(region -> row == region.getFirstRow()
                        && column >= region.getFirstColumn()
                        && column <= region.getLastColumn())
                .findAny();
    }

    @SuppressWarnings("deprecation")
    protected void parseGroups(int groupRow) {
        Row row = sheet.getRow(groupRow);
        int emptyColumnCounter = 0;
        for (int currentColumn = 0;; currentColumn++) {
            Cell cell = row.getCell(currentColumn);
            if (cell == null) {
                emptyColumnCounter++;
                if (emptyColumnCounter == 5)
                    break;
                continue;
            }
            emptyColumnCounter = 0;

            String cellValue;
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

    public abstract void parse();

    public List<Lecture> getLectures() { return lectures; }
}
