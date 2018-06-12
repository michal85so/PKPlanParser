package pl.pk.policht.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    private String name;
    private LectureType lectureType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Group> groups = new ArrayList<>();
    private List<Hour> hours = new ArrayList<>();

    enum LectureType {
        Wykład,
        Ćwiczenia,
        Laboratorium
    }
}
