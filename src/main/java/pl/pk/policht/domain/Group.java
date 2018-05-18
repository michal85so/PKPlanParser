package pl.pk.policht.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Group {
    private String name;
    private int firstCol;
    private int lastCol;
}
