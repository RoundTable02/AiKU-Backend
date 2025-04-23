package aiku_main.controller;

import aiku_main.dto.DataResDto;
import aiku_main.dto.team.TeamAddDto;
import aiku_main.dto.team.TeamDetailResDto;
import aiku_main.dto.team.TeamResDto;
import aiku_main.service.team.TeamService;
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
                                  @PathVariable Long groupId){
        Long teamId = teamService.enterTeam(memberId, groupId);

        return BaseResponse.getSimpleRes(teamId);
    }

    @PostMapping("/{groupId}/exit")
    public BaseResponse exitTeam(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                 @PathVariable Long groupId){
        Long teamId = teamService.exitTeam(memberId, groupId);

        return BaseResponse.getSimpleRes(teamId);
    }

    @GetMapping("/{groupId}")
    public BaseResponse getGroupDetail(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                       @PathVariable Long groupId){
        TeamDetailResDto result = teamService.getTeamDetail(memberId, groupId);

        return new BaseResponse(result);
    }

    @GetMapping
    public BaseResponse getGroupList(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                     @RequestParam(defaultValue = "1") int page){
        DataResDto<List<TeamResDto>> result = teamService.getTeamList(memberId, page);

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

    @GetMapping("/{groupId}/analytics/racing")
    public BaseResponse getGroupRacingResult(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                             @PathVariable Long groupId){
        String result = teamService.getTeamRacingResult(memberId, groupId);

        return new BaseResponse(result);
    }
}
