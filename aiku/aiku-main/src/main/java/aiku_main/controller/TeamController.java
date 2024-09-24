package aiku_main.controller;

import aiku_main.dto.DataResDto;
import aiku_main.dto.TeamAddDto;
import aiku_main.dto.TeamDetailResDto;
import aiku_main.dto.TeamEachListResDto;
import aiku_main.service.TeamService;
import common.response.BaseResponse;
import common.response.status.BaseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static common.response.status.BaseCode.*;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public BaseResponse addTeam(@RequestBody @Valid TeamAddDto teamDto){
        Long teamId = teamService.addTeam(null, teamDto);

        return BaseResponse.getSimpleRes(teamId, POST);
    }

    @PostMapping("/{groupId}/enter")
    public BaseResponse enterTeam(@RequestParam Long groupId){
        Long teamId = teamService.enterTeam(null, groupId);

        return BaseResponse.getSimpleRes(teamId, ENTER);
    }

    @PostMapping("/{groupId}/exit")
    public BaseResponse exitTeam(@RequestParam Long groupId){
        Long teamId = teamService.exitTeam(null, groupId);

        return BaseResponse.getSimpleRes(teamId, EXIT);
    }

    @GetMapping("/{groupId}")
    public BaseResponse getGroupDetail(@RequestParam Long groupId){
        TeamDetailResDto result = teamService.getTeamDetail(null, groupId);

        return new BaseResponse(result, GET);
    }

    @GetMapping
    public BaseResponse getGroupList(@RequestParam(defaultValue = "1") int page){
        DataResDto<List<TeamEachListResDto>> result = teamService.getTeamList(null, page);

        return new BaseResponse(result, GET);
    }
}
