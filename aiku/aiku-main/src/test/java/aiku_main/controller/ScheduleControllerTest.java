package aiku_main.controller;

import aiku_main.dto.LocationDto;
import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.service.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    ScheduleService scheduleService;
    @InjectMocks
    ScheduleController scheduleController;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addSchedule() throws Exception {
        ScheduleAddDto scheduleAddDto = new ScheduleAddDto("sche1",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now(), 0);
        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleAddDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addScheduleWithFaultDto() throws Exception {
        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(" ",
                                        new LocationDto("loc1", 1.1, 1.1),
                                        LocalDateTime.now(), 0)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto("schedule name is too long",
                                        new LocationDto("loc1", 1.1, 1.1),
                                        LocalDateTime.now(), 0)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto("name1",
                                        new LocationDto(" ", 1.1, 1.1),
                                        LocalDateTime.now(), 0)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto("name1",
                                        new LocationDto("lo1", 1.1, 1.1),
                                        null, 0)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto("name1",
                                        new LocationDto("lo1", null, 1.1),
                                        null, 0)
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto("sche1",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now());
        mockMvc.perform(patch("/groups/1/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateScheduleWithFaultDto() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto(" ",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now());
        mockMvc.perform(patch("/groups/1/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleUpdateDto)))
                .andExpect(status().isBadRequest());


    }
}