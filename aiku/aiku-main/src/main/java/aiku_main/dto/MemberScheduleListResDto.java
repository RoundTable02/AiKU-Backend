package aiku_main.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberScheduleListResDto {

    private int page;
    private int runSchedule;
    private int waitSchedule;
    private List<MemberScheduleListEachResDto> data;

    public MemberScheduleListResDto(int page, int runSchedule, int waitSchedule, List<MemberScheduleListEachResDto> data) {
        this.page = page;
        this.runSchedule = runSchedule;
        this.waitSchedule = waitSchedule;
        this.data = data;
    }
}
