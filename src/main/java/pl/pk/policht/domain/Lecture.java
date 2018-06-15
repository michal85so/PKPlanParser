package pl.pk.policht.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private LectureType lectureType;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate localDate;
    @OneToOne(cascade = CascadeType.ALL)
    private Lecturer lecturer;
    @OneToOne(cascade = CascadeType.ALL)
    private ClassRoom classRoom;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Group> groups = new ArrayList<>();
    @Transient
    private List<Hour> hours = new ArrayList<>();

    public enum LectureType {
        Wykład,
        Ćwiczenia,
        Laboratorium,
        L
    }

    public void calculateStartEndTime() {
        startTime = hours.get(0).getStartTime();
        endTime = hours.get(hours.size() - 1).getEndTime();
    }
}
