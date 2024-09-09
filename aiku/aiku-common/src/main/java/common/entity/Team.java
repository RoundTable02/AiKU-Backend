package common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team extends BaseTime{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamId")
    private Long id;
    private String teamName;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
