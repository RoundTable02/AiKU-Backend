package aiku_main.service;

import aiku_main.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    TeamService teamService;

    @Mock
    TeamRepository teamRepository;

    @Test
    @DisplayName("그룹 등록")
    void addTeam() {
        //given


        //when
        teamService.addTeam()
        //then

    }
}