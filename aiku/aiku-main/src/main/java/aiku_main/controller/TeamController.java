package aiku_main.controller;

import aiku_main.dto.DataResDto;
import aiku_main.dto.TeamAddDto;
import aiku_main.dto.TeamDetailResDto;
import aiku_main.dto.TeamEachListResDto;
import aiku_main.service.TeamService;
import common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public BaseResponse addTeam(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                @RequestBody @Valid TeamAddDto teamDto){
        Long teamId = teamService.addTeam(memberId, teamDto);

        return BaseResponse.getSimpleRes(teamId);
    }

    @PostMapping("/{groupId}/enter")
    public BaseResponse enterTeam(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                  @RequestParam Long groupId){
        Long teamId = teamService.enterTeam(memberId, groupId);

        return BaseResponse.getSimpleRes(teamId);
    }

    @PostMapping("/{groupId}/exit")
    public BaseResponse exitTeam(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                 @RequestParam Long groupId){
        Long teamId = teamService.exitTeam(memberId, groupId);

        return BaseResponse.getSimpleRes(teamId);
    }

    @GetMapping("/{groupId}")
    public BaseResponse getGroupDetail(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                       @RequestParam Long groupId){
        TeamDetailResDto result = teamService.getTeamDetail(memberId, groupId);

        return new BaseResponse(result);
    }

    @GetMapping
    public BaseResponse getGroupList(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                     @RequestParam(defaultValue = "1") int page){
        DataResDto<List<TeamEachListResDto>> result = teamService.getTeamList(memberId, page);

        return new BaseResponse(result);
    }

    @GetMapping("/{groupId}/analytics/late")
    public BaseResponse getGroupLateTimeResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                               @PathVariable Long groupId){
        String result = teamService.getTeamLateTimeResult(memberId, groupId);

        return new BaseResponse(result);
    }

    @GetMapping("/{groupId}/analytics/betting")
    public BaseResponse getGroupBettingResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                              @PathVariable Long groupId){
        String result = teamService.getTeamBettingResult(memberId, groupId);

        return new BaseResponse(result);
    }
}
