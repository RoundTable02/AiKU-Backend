package common.domain.team;

import common.domain.BaseTime;
import common.domain.Status;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static common.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team extends BaseTime {

    @Column(name = "teamId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TeamResult teamResult;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;

    private Team(String teamName) {
        this.teamName = teamName;
    }

    public static Team create(Member member, String teamName){
        Team team = new Team(teamName);
        team.addTeamMember(member);
        return team;
    }

    public void delete(){
        this.status = Status.DELETE;
    }

    public void addTeamMember(Member member){
        this.teamMembers.add(new TeamMember(this, member));
    }

    public boolean removeTeamMember(TeamMember teamMember){
        if (teamMember.getTeam().getId().equals(id)) {
            teamMember.setStatus(Status.DELETE);
            return true;
        } else {
            return false;
        }
    }

    public void setTeamLateResult(String teamLateResult){
        checkTeamResultExist();
        teamResult.setLateTimeResult(teamLateResult);
    }

    public void setTeamBettingResult(String teamBettingResult){
        checkTeamResultExist();
        teamResult.setTeamBettingResult(teamBettingResult);
    }

    private void checkTeamResultExist(){
        if (teamResult == null) {
            teamResult = new TeamResult(this);
        }
    }
}
