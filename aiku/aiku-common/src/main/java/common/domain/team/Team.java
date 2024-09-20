package common.domain.team;

import common.domain.BaseTime;
import common.domain.Status;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();

    private Team(String teamName) {
        this.teamName = teamName;
    }

    //==생성 메서드==
    public static Team create(Member member, String teamName){
        //팀 생성
        Team team = new Team(teamName);
        team.status = Status.ALIVE;

        //생성한 멤버를 팀 멤버로 추가
        team.addTeamMember(member);
        return team;
    }

    //==편의 메서드==
    public void addTeamMember(Member member){
        this.teamMembers.add(new TeamMember(this, member));
    }

    public void removeTeamMember(Member member){
        for (int i = 0; i < teamMembers.size(); i++) {
            TeamMember teamMember = teamMembers.get(i);
            if(teamMember.getMember().getId() == member.getId()){
                teamMember.setStatus(Status.DELETE);
                return;
            }
        }
    }
}
