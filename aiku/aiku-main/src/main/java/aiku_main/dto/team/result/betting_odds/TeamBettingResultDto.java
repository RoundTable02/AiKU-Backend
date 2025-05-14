package aiku_main.dto.team.result.betting_odds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamBettingResultDto {

    private Long groupId;
    private List<TeamBettingResult> members;
}
