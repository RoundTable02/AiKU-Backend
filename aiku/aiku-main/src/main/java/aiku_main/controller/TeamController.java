package aiku_main.controller;

import aiku_main.dto.TeamAddDto;
import aiku_main.service.TeamService;
import common.response.BaseResponse;
import common.response.status.BaseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public BaseResponse addTeam(@RequestBody TeamAddDto teamDto){
        Long teamId = teamService.addTeam(null, teamDto);

        return BaseResponse.getSimpleRes(teamId, BaseCode.POST);
    }

    @PostMapping("/{groupId}/enter")
    public BaseResponse enterTeam(@RequestParam Long groupId){
        Long teamId = teamService.enterTeam(null, groupId);

        return BaseResponse.getSimpleRes(teamId, BaseCode.ENTER);
    }

    @PostMapping("/{groupId}/exit")
    public BaseResponse exitTeam(@RequestParam Long groupId){
        Long teamId = teamService.exitTeam(null, groupId);

        return BaseResponse.getSimpleRes(teamId, BaseCode.EXIT);
    }

}
