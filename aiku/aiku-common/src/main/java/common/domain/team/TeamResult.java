package common.domain.team;

import common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TeamResult extends BaseTime {

    @Column(name = "teamResultId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "teamId")
    @OneToOne(fetch = FetchType.LAZY)
    private Team team;

    @Column(columnDefinition = "MEDIUMTEXT")
    @Lob
    private String lateTimeResult;

    @Column(columnDefinition = "MEDIUMTEXT")
    @Lob
    private String teamBettingResult;

    @Column(columnDefinition = "MEDIUMTEXT")
    @Lob
    private String teamRacingResult;

    protected TeamResult(Team team) {
        this.team = team;
    }

    protected void setLateTimeResult(String lateTimeResult) {
        this.lateTimeResult = lateTimeResult;
    }

    protected void setTeamBettingResult(String teamBettingResult) {
        this.teamBettingResult = teamBettingResult;
    }

    protected void setTeamRacingResult(String teamRacingResult) {
        this.teamRacingResult = teamRacingResult;
    }

    public boolean hasNoLateTimeResult(){
        return lateTimeResult == null;
    }

    public boolean hasNoTeamBettingResult(){
        return teamBettingResult == null;
    }

    public boolean hasNoTeamRacingResult(){
        return teamRacingResult == null;
    }
}
