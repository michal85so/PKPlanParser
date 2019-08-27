package pl.pk.policht.util;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.dao.LectureDao;
import pl.pk.policht.dao.LectureNameDao;
import pl.pk.policht.dao.LectureTypeDao;
import pl.pk.policht.dao.LecturerDao;
import pl.pk.policht.domain.Date;
import pl.pk.policht.domain.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ExtramuralStudiesDataParser extends AbstractDataParser {
    private static final Logger logger = Logger.getLogger(ExtramuralStudiesDataParser.class);
    private final LectureDao lectureDao;

    private List<Date> dates = new ArrayList<>();
    private Map<Date, Map<Integer, Hour>> hours = new HashMap<>();

    public ExtramuralStudiesDataParser(Sheet sheet, LecturerDao lecturerDao, LectureTypeDao lectureTypeDao, LectureNameDao lectureNameDao, LectureDao lectureDao) {
        super(sheet, lecturerDao, lectureTypeDao, lectureNameDao);
        this.lectureDao = lectureDao;
    }

    public void parse() {
        parseDates();
        parseGroups(dates.get(0).getFirstRow() - 1);
        parseHours();
        parseLectures();
        showResults();
    }

    void showResults() {
        dates.forEach(logger::debug);
        groups.forEach(logger::debug);
        hours.values().forEach(logger::info);
        lectures.forEach(logger::debug);
    }

    @SuppressWarnings("deprecation")
    private void parseDates() {
        for (int rowIterator = 0; rowIterator < sheet.getPhysicalNumberOfRows(); rowIterator++) {
            Row row = sheet.getRow(rowIterator);

            if (row == null) continue;

            Cell cell = row.getCell(DATE_COLUMN_POSITION);
            if (cell != null && CellType.NUMERIC.getCode() == cell.getCellType()) {
                LocalDate localDate = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Optional<CellRangeAddress> rangeAddress = checkDateIsInMergedRowRegions(row.getRowNum());
                rangeAddress.ifPresent(address -> dates.add(new Date(localDate, address.getFirstRow(), address.getLastRow())));
            }
        }
    }

    private void parseHours() {
        final int columnWithHours = 1;
        for (Date date : dates) {
            hours.put(date, new HashMap<>());
            for (int hourCurrentRow = date.getFirstRow(); hourCurrentRow <= date.getLastRow(); hourCurrentRow++) {
                Row row = sheet.getRow(hourCurrentRow);
                Cell cell = row.getCell(columnWithHours);
                hours.get(date).put(hourCurrentRow, new Hour(cell.getStringCellValue(), hourCurrentRow));
            }
        }
    }

    private void parseLectures() {
        for (Date date : dates) {
            for (Group group : groups) {
                for (int groupCurrentColumn = group.getFirstCol(); groupCurrentColumn <= group.getLastCol(); groupCurrentColumn++) {
                    for (int dateCurrentRow = date.getFirstRow(); dateCurrentRow <= date.getLastRow(); dateCurrentRow++) {
                        Row row = sheet.getRow(dateCurrentRow);
                        Cell cell = row.getCell(groupCurrentColumn);
                        if (cell == null)
                            continue;
                        String cellValue = cell.getStringCellValue();
                        if (cellValue != null && !cellValue.isEmpty()) {
                            Optional<CellRangeAddress> mergedRegion = checkIsInMergedColumnRegions(dateCurrentRow, groupCurrentColumn);
                            if (mergedRegion.isPresent()) {
                                createLecture(date, cellValue, mergedRegion.get());
                            } else {
                                logger.warn(cellValue + " row " + row.getRowNum() + " column " + cell.getColumnIndex());
                            }
                        }
                    }
                }
            }
        }
    }

    private void createLecture(Date date, String value, CellRangeAddress region) {
        Lecture lecture = parseLectureValue(value);
        for (int regionCurrentRow = region.getFirstRow(); regionCurrentRow <= region.getLastRow(); regionCurrentRow++) {
            Hour hour = hours.get(date).get(regionCurrentRow);
            lecture.getHours().add(hour);
        }
        for (int regionCurrentColumn = region.getFirstColumn(); regionCurrentColumn <= region.getLastColumn(); regionCurrentColumn++) {
            int cc = regionCurrentColumn;
            groups.stream()
                    .filter(group -> cc >= group.getFirstCol() && cc <= group.getLastCol())
                    .forEach(group -> {
                        if (lectureGroups.get(group.getName()) == null) {
                            lectureGroups.put(group.getName(), group);
                            group.getLectures().add(lecture);
                        }
                        lecture.getGroups().add(lectureGroups.get(group.getName()));
                    });
        }
        lecture.setLocalDate(date.getDate());
        lecture.calculateStartEndTime();
        lectures.add(lecture);
        lectureDao.save(lecture);
    }

    private Lecture parseLectureValue(String text) {
        Lecture lecture = new Lecture();
        String[] strings = text.split("\\r?\\n");
        int i = 0;

        lecture.setLectureName(getLectureName(strings[i++]));
        lecture.setLectureType(getLectureType(strings[i++]));
        lecture.setLecturer(getLecturer(strings[i++]));
        lecture.setClassRoom(getClassRoom(strings[i]));

        return lecture;
    }

    private LectureName getLectureName(String string) {
        LectureName existingLectureName = lectureNameDao.findByName(string);
        if (existingLectureName != null) {
            return existingLectureName;
        }
        LectureName lectureName = new LectureName();
        lectureName.setName(string);
        return lectureName;
    }

    private LectureType getLectureType(String string) {
        return lectureTypeDao.findByName(string);
    }

    private Lecturer getLecturer(String string) {
        Lecturer lecturer = lecturers.get(string);
        if (lecturer != null)
            return lecturer;

        lecturer = lecturerDao.findByName(string);
        if (lecturer != null) {
            lecturers.put(lecturer.getName(), lecturer);
            return lecturer;
        }
        logger.error("there is no lecturer like: " + string);
//        throw new Exception("there is no lecturer like: " + string);
        return null;
    }

    private ClassRoom getClassRoom(String string) {
        ClassRoom classRoom = classRooms.get(string);
        if (classRoom != null)
            return classRoom;
        classRoom = new ClassRoom(string);
        classRooms.put(classRoom.getName(), classRoom);
        return classRoom;
    }
}
