package pl.pk.policht.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
public class Date {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    private LocalDate date;
    @NonNull
    private int firstRow;
    @NonNull
    private int lastRow;
}
