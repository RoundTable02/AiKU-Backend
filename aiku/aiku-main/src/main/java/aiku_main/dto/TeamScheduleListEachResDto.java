package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.ExecStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamScheduleListEachResDto {
    private Long scheduleId;
    private String scheduleName;
    private LocationDto location;
    private LocalDateTime scheduleTime;
    private ExecStatus scheduleStatus;
    private boolean accept;

    @QueryProjection
    public TeamScheduleListEachResDto(Long scheduleId, String scheduleName, LocationDto location, LocalDateTime scheduleTime, ExecStatus scheduleStatus, Long memberId) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.location = location;
        this.scheduleTime = scheduleTime;
        this.scheduleStatus = scheduleStatus;
        this.accept = (memberId == null) ? false : true;
    }
}
