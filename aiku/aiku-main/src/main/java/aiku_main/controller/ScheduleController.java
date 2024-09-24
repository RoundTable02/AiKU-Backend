package aiku_main.controller;

import aiku_main.dto.*;
import aiku_main.service.ScheduleService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import common.response.status.BaseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping({"/groups/{groupId}/schedules", "/member"})
@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping()
    public BaseResponse<BaseResultDto> addSchedule(@PathVariable Long groupId,
                                                   @RequestBody @Valid ScheduleAddDto scheduleDto){
        Long addId = scheduleService.addSchedule(null, groupId, scheduleDto);

        return BaseResponse.getSimpleRes(addId, BaseCode.POST);
    }

    @PatchMapping("/{scheduleId}")
    public BaseResponse<BaseResultDto> updateSchedule(@PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody @Valid ScheduleUpdateDto scheduleDto){
        Long updateId = scheduleService.updateSchedule(null, scheduleId, scheduleDto);

        return BaseResponse.getSimpleRes(updateId, BaseCode.PATCH);
    }

    @PostMapping("/{scheduleId}/enter")
    public BaseResponse<BaseResultDto> enterSchedule(@PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody ScheduleEnterDto enterDto){
        Long enterId = scheduleService.enterSchedule(null, groupId, scheduleId, enterDto);

        return BaseResponse.getSimpleRes(enterId, BaseCode.ENTER);
    }

    @PostMapping("/{scheduleId}/exit")
    public BaseResponse<BaseResultDto> exitSchedule(@PathVariable Long groupId,
                                                     @PathVariable Long scheduleId){
        Long exitId = scheduleService.exitSchedule(null, groupId, scheduleId);

        return BaseResponse.getSimpleRes(exitId, BaseCode.ENTER);
    }

    @GetMapping("/{scheduleId}")
    public BaseResponse<ScheduleDetailResDto> getScheduleDetail(@PathVariable Long groupId,
                                                                @PathVariable Long scheduleId){
        ScheduleDetailResDto result = scheduleService.getScheduleDetail(null, groupId, scheduleId);

        return new BaseResponse<>(result, BaseCode.GET);
    }

    @GetMapping
    public BaseResponse<TeamScheduleListResDto> getTeamScheduleList(@PathVariable Long groupId,
                                                                    @ModelAttribute SearchDateCond dateCond,
                                                                    @RequestParam(defaultValue = "1") int page){
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(null, groupId, dateCond, page);

        return new BaseResponse<>(result, BaseCode.GET);
    }

    @GetMapping("/schedules")
    public BaseResponse<MemberScheduleListResDto> getMemberScheduleList(@ModelAttribute SearchDateCond dateCond,
                                                                    @RequestParam(defaultValue = "1") int page){
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(null, dateCond, page);

        return new BaseResponse<>(result, BaseCode.GET);
    }
}
