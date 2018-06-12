package pl.pk.policht.domain;

import com.sun.istack.internal.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Setter
@Getter
@ToString
public class Hour {
    @Setter(AccessLevel.NONE)
    private String range;
    private int row;
    @Setter(AccessLevel.NONE)
    private LocalTime startTime;
    @Setter(AccessLevel.NONE)
    private LocalTime endTime;

    public Hour(@NotNull String range, @NotNull int row) {
        setRange(range);
        this.row = row;
    }

    public void setRange(@NotNull String range) {
        parseRangeToStartEndTime(range);
        this.range = range;
    }

    private void parseRangeToStartEndTime(String range) {
        range = range.replace(".",":");
        String[] strings = range.split("-");
        String[] startTimeStringArray = strings[0].split(":");
        String[] endTimeStringArray = strings[1].split(":");
        startTime = LocalTime.of(Integer.parseInt(startTimeStringArray[0].trim()), Integer.parseInt(startTimeStringArray[1].trim()));
        endTime = LocalTime.of(Integer.parseInt(endTimeStringArray[0].trim()), Integer.parseInt(endTimeStringArray[1].trim()));
    }
}
