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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TeamResult teamResult;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.ALIVE;

    private Team(String teamName) {
        this.teamName = teamName;
    }

    //==CUD 메서드==
    public static Team create(Member member, String teamName){
        //팀 생성
        Team team = new Team(teamName);

        //생성한 멤버를 팀 멤버로 추가
        team.addTeamMember(member);
        return team;
    }

    public void delete(){
        this.status = Status.DELETE;
    }

    //==편의 메서드==
    public void addTeamMember(Member member){
        this.teamMembers.add(new TeamMember(this, member));
    }

    public void removeTeamMember(TeamMember teamMember){
        teamMember.setStatus(Status.DELETE);
    }

    public void setTeamLateResult(String teamLateResult){
        checkTeamResultExist();
        teamResult.setLateTimeResult(teamLateResult);
    }

    public void setTeamBettingResult(String teamBettingResult){
        checkTeamResultExist();
        teamResult.setTeamBettingResult(teamBettingResult);
    }

    public LocalDateTime getTeamResultLastModifiedAt(){
        if (teamResult == null) {
            return null;
        }
        return teamResult.getModifiedAt();
    }

    private void checkTeamResultExist(){
        if (teamResult == null) {
            teamResult = new TeamResult(this);
        }
    }
}
