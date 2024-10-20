package map.controller;

import common.domain.member.Member;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.dto.*;
import map.service.MapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping({"/map/{scheduleId}"})
@RequiredArgsConstructor
@RestController
public class MapController {

    private final MapService mapService;

    @PostMapping("/location")
    public BaseResponse<BaseResultDto> sendLocation(@PathVariable Long scheduleId,
                                                    @RequestBody Long accessMemberId,
                                                    @RequestBody @Valid RealTimeLocationDto realTimeLocationDto) {
        Long scheduleResId = mapService.sendLocation(accessMemberId, scheduleId, realTimeLocationDto);

        return BaseResponse.getSimpleRes(scheduleResId);
    }

    @PostMapping("/arrival")
    public BaseResponse<BaseResultDto> makeMemberArrive(@PathVariable Long scheduleId,
                                                        @RequestBody Long accessMemberId,
                                                        @RequestBody @Valid MemberArrivalDto arrivalDto) {
        Long scheduleResId = mapService.makeMemberArrive(accessMemberId, scheduleId, arrivalDto);

        return BaseResponse.getSimpleRes(scheduleResId);
    }

    @PostMapping("/emoji")
    public BaseResponse<BaseResultDto> sendEmoji(@PathVariable Long scheduleId,
                                                        @RequestBody Long accessMemberId,
                                                        @RequestBody @Valid EmojiDto emojiDto) {
        Long scheduleResId = mapService.sendEmoji(accessMemberId, scheduleId, emojiDto);

        return BaseResponse.getSimpleRes(scheduleResId);
    }

    @GetMapping("/racing")
    public BaseResponse<DataResDto> getRacings(@PathVariable Long scheduleId,
                                               @RequestBody Long accessMemberId) {
        DataResDto<List<RacingResDto>> racings = mapService.getRacings(accessMemberId, scheduleId);

        return new BaseResponse<>(racings);
    }

    @PostMapping("/racing")
    public BaseResponse<BaseResultDto> makeRacing(@PathVariable Long scheduleId,
                                                  @RequestBody Long accessMemberId,
                                                  @RequestBody @Valid RacingAddDto racingAddDto) {
        Long racingId = mapService.makeRacing(accessMemberId, scheduleId, racingAddDto);

        return BaseResponse.getSimpleRes(racingId);
    }

    @PostMapping("/racing/{racingId}/accept")
    public BaseResponse<BaseResultDto> acceptRacing(@PathVariable Long scheduleId,
                                                    @PathVariable Long racingId,
                                                    @RequestBody Long accessMemberId) {
        Long acceptRacingId = mapService.acceptRacing(accessMemberId, scheduleId, racingId);

        return BaseResponse.getSimpleRes(acceptRacingId);
    }

    @PostMapping("/racing/{racingId}/deny")
    public BaseResponse<BaseResultDto> denyRacing(@PathVariable Long scheduleId,
                                                  @PathVariable Long racingId,
                                                  @RequestBody Long accessMemberId) {
        Long deniedRacingId = mapService.denyRacing(accessMemberId, scheduleId, racingId);

        return BaseResponse.getSimpleRes(deniedRacingId);
    }


}
