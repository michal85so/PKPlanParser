package pl.pk.policht.domain;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "day")
@ToString
public class DayOfWeek {
    @NonNull
    private String day;
    @NonNull
    private int firstRow;
    @NonNull
    private int lastRow;
}
