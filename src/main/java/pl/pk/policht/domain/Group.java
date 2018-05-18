package pl.pk.policht.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class Group {
    private String name;
    private int firstCol;
    private int lastCol;
}
