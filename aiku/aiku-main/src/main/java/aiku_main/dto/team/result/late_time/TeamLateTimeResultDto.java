package aiku_main.dto.team.result.late_time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamLateTimeResultDto {

    private Long groupId;
    private List<TeamLateTimeResult> members;
}
