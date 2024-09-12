package aiku_main.controller;

import aiku_main.dto.TeamAddDto;
import aiku_main.service.TeamService;
import common.response.BaseResponse;
import common.response.status.BaseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
