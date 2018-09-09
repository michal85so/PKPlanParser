package pl.pk.policht.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@NoArgsConstructor
@Setter
@Getter
//@ToString
@Entity(name = "group_name")
@EqualsAndHashCode(of = "name")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    private String name;
    @NonNull
    private int firstCol;
    @NonNull
    private int lastCol;
    @ManyToMany
    private Set<Lecture> lectures = new HashSet<>();
}
