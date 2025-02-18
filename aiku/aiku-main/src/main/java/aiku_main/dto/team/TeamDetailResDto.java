package aiku_main.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamDetailResDto {

    private Long groupId;
    private String groupName;
    private List<TeamMemberResDto> members;
}
