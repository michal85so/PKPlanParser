package pl.pk.policht.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode(of = "id")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private LectureName lectureName;
    @ManyToOne(cascade = CascadeType.ALL)
    private LectureType lectureType;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate localDate;
    @ManyToOne(cascade = CascadeType.ALL)
    private Lecturer lecturer;
    @OneToOne(cascade = CascadeType.ALL)
    private ClassRoom classRoom;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Lecture_group_name",
            joinColumns = @JoinColumn(name = "lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "group_name_id"),
            uniqueConstraints = @UniqueConstraint(name = "lecture_group_name_constraint",
                    columnNames = {"lecture_id", "group_name_id"}))
    private Set<Group> groups = new HashSet<>();
    @Transient
    private List<Hour> hours = new ArrayList<>();
    private String note;

    public void calculateStartEndTime() {
        startTime = hours.get(0).getStartTime();
        endTime = hours.get(hours.size() - 1).getEndTime();
    }
}
