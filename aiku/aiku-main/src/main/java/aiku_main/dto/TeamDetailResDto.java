package aiku_main.dto;

import common.domain.Team;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamDetailResDto {
    private Long groupId;
    private String groupName;
    private List<TeamMemberResDto> members = new ArrayList<>();

    public TeamDetailResDto(Team team) {
        this.groupId = team.getId();
        this.groupName = team.getTeamName();

        team.getTeamMembers().forEach(TeamMemberResDto::new);
    }
}
