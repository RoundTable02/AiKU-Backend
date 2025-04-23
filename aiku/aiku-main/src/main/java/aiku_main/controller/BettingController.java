package aiku_main.controller;

import aiku_main.dto.betting.BettingAddDto;
import aiku_main.service.betting.BettingService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/schedules/{scheduleId}/bettings")
@RequiredArgsConstructor
@RestController
public class BettingController {

    private final BettingService bettingService;

    @PostMapping
    public BaseResponse<BaseResultDto> addBetting(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                  @PathVariable Long scheduleId,
                                                  @RequestBody @Valid BettingAddDto bettingDto){
        Long resultId = bettingService.addBetting(memberId, scheduleId, bettingDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{bettingId}")
    public BaseResponse<BaseResultDto> cancelBetting(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                     @PathVariable Long scheduleId,
                                                     @PathVariable Long bettingId,
                                                     @RequestBody BettingAddDto bettingDto){
        Long resultId = bettingService.cancelBetting(memberId, scheduleId, bettingId);

        return BaseResponse.getSimpleRes(resultId);
    }
}
