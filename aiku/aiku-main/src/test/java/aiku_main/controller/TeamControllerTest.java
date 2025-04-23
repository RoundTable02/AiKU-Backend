package aiku_main.controller;

import aiku_main.controller.team.TeamController;
import aiku_main.dto.team.TeamAddDto;
import aiku_main.service.team.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    TeamService teamService;
    @InjectMocks
    TeamController teamController;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController)
                .build();
    }

    @Test
    void addTeam() throws Exception {
        TeamAddDto teamAddDto = new TeamAddDto("group name");
        mockMvc.perform(post("/groups")
                .header("Access-Member-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamAddDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addTeamWithFaultDto() throws Exception {
        mockMvc.perform(post("/groups").header("Access-Member-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamAddDto(" "))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TeamAddDto("group name is too long"))))
                .andExpect(status().isBadRequest());
    }
}