package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TeamResult extends BaseTime{

    @Column(name = "teamResultId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "teamId")
    @OneToOne(fetch = FetchType.LAZY)
    private Team team;

    private String lateTimeResult;
    private String teamBettingResult;
    private String teamRacingResult;
}
