package aiku_main.controller;

import aiku_main.dto.*;
import aiku_main.service.ScheduleService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
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
    public BaseResponse<BaseResultDto> addSchedule(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                   @PathVariable Long groupId,
                                                   @RequestBody @Valid ScheduleAddDto scheduleDto){
        Long addId = scheduleService.addSchedule(memberId, groupId, scheduleDto);

        return BaseResponse.getSimpleRes(addId);
    }

    @PatchMapping("/{scheduleId}")
    public BaseResponse<BaseResultDto> updateSchedule(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                      @PathVariable Long groupId,
                                                      @PathVariable Long scheduleId,
                                                      @RequestBody @Valid ScheduleUpdateDto scheduleDto){
        Long updateId = scheduleService.updateSchedule(memberId, scheduleId, scheduleDto);

        return BaseResponse.getSimpleRes(updateId);
    }

    @PostMapping("/{scheduleId}/enter")
    public BaseResponse<BaseResultDto> enterSchedule(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                     @PathVariable Long groupId,
                                                     @PathVariable Long scheduleId,
                                                     @RequestBody @Valid ScheduleEnterDto enterDto){
        Long enterId = scheduleService.enterSchedule(memberId, groupId, scheduleId, enterDto);

        return BaseResponse.getSimpleRes(enterId);
    }

    @PostMapping("/{scheduleId}/exit")
    public BaseResponse<BaseResultDto> exitSchedule(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                    @PathVariable Long groupId,
                                                    @PathVariable Long scheduleId){
        Long exitId = scheduleService.exitSchedule(memberId, groupId, scheduleId);

        return BaseResponse.getSimpleRes(exitId);
    }

    @GetMapping("/{scheduleId}")
    public BaseResponse<ScheduleDetailResDto> getScheduleDetail(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                                @PathVariable Long groupId,
                                                                @PathVariable Long scheduleId){
        ScheduleDetailResDto result = scheduleService.getScheduleDetail(memberId, groupId, scheduleId);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<TeamScheduleListResDto> getTeamScheduleList(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                                    @PathVariable Long groupId,
                                                                    @ModelAttribute SearchDateCond dateCond,
                                                                    @RequestParam(defaultValue = "1") int page){
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(memberId, groupId, dateCond, page);

        return new BaseResponse<>(result);
    }

    @GetMapping("/schedules")
    public BaseResponse<MemberScheduleListResDto> getMemberScheduleList(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                                        @ModelAttribute SearchDateCond dateCond,
                                                                        @RequestParam(defaultValue = "1") int page){
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(memberId, dateCond, page);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{scheduleId}/arrival/result")
    public BaseResponse<String> getScheduleArrivalResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                         @PathVariable Long groupId,
                                                         @PathVariable Long scheduleId){
        String result = scheduleService.getScheduleArrivalResult(memberId, groupId, scheduleId);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{scheduleId}/betting/result")
    public BaseResponse<String> getScheduleBettingResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                         @PathVariable Long groupId,
                                                         @PathVariable Long scheduleId){
        String result = scheduleService.getScheduleBettingResult(memberId, groupId, scheduleId);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{scheduleId}/racing/result")
    public BaseResponse<String> getScheduleRacingResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                         @PathVariable Long groupId,
                                                         @PathVariable Long scheduleId){
        String result = scheduleService.getScheduleRacingResult(memberId, groupId, scheduleId);

        return new BaseResponse<>(result);
    }
}
