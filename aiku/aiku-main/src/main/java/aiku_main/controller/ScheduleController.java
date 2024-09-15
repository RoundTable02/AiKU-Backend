package aiku_main.controller;

import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleEnterDto;
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
        Long addId = scheduleService.addSchedule(null, groupId, scheduleDto);

        return BaseResponse.getSimpleRes(addId, BaseCode.POST);
    }

    @PatchMapping("/{scheduleId}")
    public BaseResponse<BaseResultDto> updateSchedule(@PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody ScheduleUpdateDto scheduleDto){
        Long updateId = scheduleService.updateSchedule(null, scheduleId, scheduleDto);

        return BaseResponse.getSimpleRes(updateId, BaseCode.PATCH);
    }

    @PostMapping("/{scheduleId}/enter")
    public BaseResponse<BaseResultDto> enderSchedule(@PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody ScheduleEnterDto enterDto){
        Long enterId = scheduleService.enterSchedule(null, groupId, scheduleId, enterDto);

        return BaseResponse.getSimpleRes(enterId, BaseCode.ENTER);
    }

    @PostMapping("/{scheduleId}/exit")
    public BaseResponse<BaseResultDto> enderSchedule(@PathVariable Long groupId,
                                                     @PathVariable Long scheduleId){
        Long exitId = scheduleService.exitSchedule(null, groupId, scheduleId);

        return BaseResponse.getSimpleRes(exitId, BaseCode.ENTER);
    }
}
