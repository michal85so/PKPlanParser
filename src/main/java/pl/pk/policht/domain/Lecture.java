package pl.pk.policht.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Lecture {
    @NonNull
    private String name;
    private List<Group> groups = new ArrayList<>();
    private List<Hour> hours = new ArrayList<>();
}
