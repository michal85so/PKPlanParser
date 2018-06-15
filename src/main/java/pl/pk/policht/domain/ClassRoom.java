package pl.pk.policht.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode(of = "name")
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    @Column(nullable = false, unique = true)
    private String name;
}
