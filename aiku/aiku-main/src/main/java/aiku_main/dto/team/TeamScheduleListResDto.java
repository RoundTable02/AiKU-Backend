package aiku_main.dto.team;

import common.domain.team.Team;
import lombok.Getter;

import java.util.List;

@Getter
public class TeamScheduleListResDto {

    private int page;
    private Long groupId;
    private int runSchedule;
    private int waitSchedule;
    private List<TeamScheduleListEachResDto> data;

    public TeamScheduleListResDto(Team team, int page, int runSchedule, int waitSchedule, List<TeamScheduleListEachResDto> data) {
        this.groupId = team.getId();
        this.page = page;
        this.runSchedule = runSchedule;
        this.waitSchedule = waitSchedule;
        this.data = data;
    }
}
