package pl.pk.policht.util;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.pk.policht.dao.LectureNameDao;
import pl.pk.policht.dao.LectureTypeDao;
import pl.pk.policht.dao.LecturerDao;
import pl.pk.policht.domain.*;

import java.util.*;

public class FullTimeStudiesDataParser extends AbstractDataParser {
    private static final Logger logger = Logger.getLogger(FullTimeStudiesDataParser.class);

    private final String[] DAYS_OF_WEEK = {"Poniedziałek" , "Wtorek", "Środa", "Czwartek", "Piątek"};

    private List<DayOfWeek> dates = new ArrayList<>();
    Map<DayOfWeek, Map<Integer, Hour>> hours = new HashMap<>();

    public FullTimeStudiesDataParser(Sheet sheet, LecturerDao lecturerDao, LectureTypeDao lectureTypeDao, LectureNameDao lectureNameDao) {
        super(sheet, lecturerDao, lectureTypeDao, lectureNameDao);
    }

    @Override
    public void parse() {
        parseDays();
        parseGroups(dates.get(0).getFirstRow() - 1);
        parseHours();
        parseLectures();
        showResults();
    }

    void showResults() {
        dates.forEach(logger::debug);
        groups.forEach(logger::debug);
        lectures.forEach(logger::debug);
        hours.values().forEach(logger::info);
        lectures.forEach(logger::info);
    }

    private void parseDays() {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(0);
            if (cell != null && CellType.STRING.getCode() == cell.getCellType()) {
                String value = cell.getStringCellValue();
                for (String dayOfWeek : DAYS_OF_WEEK) {
                    if (dayOfWeek.equalsIgnoreCase(value)) {
                        Optional<CellRangeAddress> rangeAddress = checkDateIsInMergedRowRegions(row.getRowNum());
                        rangeAddress.ifPresent(address -> dates.add(new DayOfWeek(dayOfWeek, address.getFirstRow(), address.getLastRow())));
                    }
                }
            }
        }
    }

    private void parseHours() {
        final int columnWithHours = 1;
        for (DayOfWeek date : dates) {
            hours.put(date, new HashMap<>());
            for (int hourCurrentRow = date.getFirstRow(); hourCurrentRow <= date.getLastRow(); hourCurrentRow++) {
                Row row = sheet.getRow(hourCurrentRow);
                Cell cell = row.getCell(columnWithHours);
                if (cell.getStringCellValue() != null && !cell.getStringCellValue().isEmpty())
                    hours.get(date).put(hourCurrentRow, new Hour(cell.getStringCellValue(), hourCurrentRow));
            }
        }
    }

    private void parseLectures() {
        for (DayOfWeek date : dates) {
            for (Group group : groups) {
                for (int groupCurrentColumn = group.getFirstCol(); groupCurrentColumn <= group.getLastCol(); groupCurrentColumn++) {
                    for (int dateCurrentRow = date.getFirstRow(); dateCurrentRow <= date.getLastRow(); dateCurrentRow++) {
                        Row row = sheet.getRow(dateCurrentRow);
                        Cell cell = row.getCell(groupCurrentColumn);
                        if (cell == null)
                            continue;
                        if (CellType.NUMERIC == cell.getCellTypeEnum())
                            continue;
                        String value = cell.getStringCellValue();
                        if (value != null && !value.isEmpty()) {
                            Optional<CellRangeAddress> mergedRegion = checkIsInMergedColumnRegions(dateCurrentRow, groupCurrentColumn);
                            if (mergedRegion.isPresent()) {
                                Lecture lecture = parseLectureValue(value);
                                CellRangeAddress region = mergedRegion.get();
                                for (int regionCurrentRow = region.getFirstRow(); regionCurrentRow <= region.getLastRow(); regionCurrentRow++) {
                                    Hour hour = hours.get(date).get(regionCurrentRow);
                                    if (hour != null)
                                        lecture.getHours().add(hour);
                                }
                                for (int regionCurrentColumn = region.getFirstColumn(); regionCurrentColumn <= region.getLastColumn(); regionCurrentColumn++) {
                                    for (Group innerGroup : groups) {
                                        if (regionCurrentColumn >= innerGroup.getFirstCol() && regionCurrentColumn <= innerGroup.getLastCol()) {
                                            if (lectureGroups.get(innerGroup.getName()) == null) {
                                                lectureGroups.put(innerGroup.getName(), innerGroup);
                                                innerGroup.getLectures().add(lecture);
                                            }
                                            lecture.getGroups().add(lectureGroups.get(innerGroup.getName()));
                                        }
                                    }
                                }
                                lecture.setDayOfWeek(date.getDay());
                                lecture.calculateStartEndTime();
                                lectures.add(lecture);
                            }
                            else {
                                logger.warn(value + " row " + row.getRowNum() + " column " + cell.getColumnIndex());
                            }
                        }
                    }
                }
            }
        }
        System.out.println("done");
    }

    private Lecture parseLectureValue(String text) {
        Lecture lecture = new Lecture();
        String[] strings = text.split("\\r?\\n");
        int i = 0;
        LectureName lectureName = lectureNameDao.findByName(strings[i++]);
        if (lectureName != null) {
            lecture.setLectureName(lectureName);
        }

        LectureType lectureType = lectureTypeDao.findByName(strings[i]);
        if (lectureType != null) {
            lecture.setLectureType(lectureType);
            i++;
        }

        if (lecturers.get(strings[i]) == null) {
            Lecturer byName = lecturerDao.findByName(strings[i]);
            if (byName != null){
                lecturers.put(byName.getName(), byName);
                lecture.setLecturer(byName);
            }
        }
        else
            lecture.setLecturer(lecturers.get(strings[i++]));
        if (strings.length > i) {
            if (classRooms.get(strings[i]) == null) {
                ClassRoom classRoom = new ClassRoom(strings[i++]);
                classRooms.put(classRoom.getName(), classRoom);
                lecture.setClassRoom(classRoom);
            }
            else
                lecture.setClassRoom(classRooms.get(strings[i++]));
        }
        if (strings.length > i) {
            lecture.setNote(strings[i]);
        }

        return lecture;
    }
}
