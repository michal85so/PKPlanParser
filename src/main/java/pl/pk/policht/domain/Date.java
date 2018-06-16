package pl.pk.policht.domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Date {
    @NonNull
    private LocalDate date;
    @NonNull
    private int firstRow;
    @NonNull
    private int lastRow;
}
