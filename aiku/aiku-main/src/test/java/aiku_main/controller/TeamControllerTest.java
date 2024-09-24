package aiku_main.controller;

import aiku_main.dto.TeamAddDto;
import aiku_main.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamControllerTest {

    @Mock
    TeamService teamService;

    @InjectMocks
    TeamController teamController;

    @Test
    @DisplayName("그룹 등록-이름이 공백일때 오류")
    void addTeam() {
        //when
        TeamAddDto teamAddDto = new TeamAddDto("  ");
        assertThatThrownBy(() -> teamController.addTeam(teamAddDto)).isInstanceOf(NullPointerException.class);
    }
}