package pl.pk.policht.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class DateRowRange {
    private LocalDate date;
    private int firstRow;
    private int lastRow;
}
