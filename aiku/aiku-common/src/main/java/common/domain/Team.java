package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team extends BaseTime{

    @Column(name = "teamId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String teamName;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
