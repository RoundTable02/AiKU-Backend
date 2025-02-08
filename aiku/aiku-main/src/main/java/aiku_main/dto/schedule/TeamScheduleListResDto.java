package aiku_main.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamScheduleListResDto {

    private int page;
    private Long groupId;
    private int runSchedule;
    private int waitSchedule;
    private List<TeamScheduleListEachResDto> data;
}
