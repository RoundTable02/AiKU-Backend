package aiku_main.dto.schedule;

import aiku_main.dto.LocationDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SchedulePreviewResDto {

    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime scheduleTime;
    private LocationDto location;
    private ScheduleOwnerResDto owner;

    @QueryProjection
    public SchedulePreviewResDto(Long scheduleId,
                                 String scheduleName,
                                 LocalDateTime scheduleTime,
                                 LocationDto location,
                                 ScheduleOwnerResDto owner) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
        this.owner = owner;
    }
}
