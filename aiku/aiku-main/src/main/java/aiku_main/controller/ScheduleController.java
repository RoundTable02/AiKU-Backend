package aiku_main.controller;

import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.service.ScheduleService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import common.response.status.BaseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping({"/groups/{groupId}/schedules", "/schedules"})
@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping()
    public BaseResponse<BaseResultDto> addSchedule(@PathVariable Long groupId,
                                                   @RequestBody ScheduleAddDto scheduleDto){
        Long addId = scheduleService.addSchedule(null, scheduleDto);

        return BaseResponse.getSimpleRes(updateId, BaseCode.PATCH);
    }

    @PatchMapping("/{scheduleId}")
    public BaseResponse<BaseResultDto> updateSchedule(@PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody ScheduleUpdateDto scheduleDto){
        Long updateId = scheduleService.updateSchedule(null, scheduleId, scheduleDto);

        return BaseResponse.getSimpleRes(updateId, BaseCode.PATCH);
    }
}
