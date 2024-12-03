package aiku_main.dto.team;

import lombok.Getter;

import java.util.List;

@Getter
public class TeamDetailResDto {

    private Long groupId;
    private String groupName;
    private List<TeamMemberResDto> members;

    public TeamDetailResDto(Long groupId, String groupName, List<TeamMemberResDto> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
    }
}
