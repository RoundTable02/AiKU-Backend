package aiku_main.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamBettingResult {
    private Long groupId;
    private List<TeamResultMember> members;
}
