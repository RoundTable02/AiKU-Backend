package aiku_main.application_event.event;

import common.domain.member.Member;
import common.domain.team.Team;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.TeamValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TeamExitEvent{

    private MemberValue member;
    private TeamValue team;

    public TeamExitEvent(Member member, Team team) {
        this.member = new MemberValue(member);
        this.team = new TeamValue(team);
    }
}
