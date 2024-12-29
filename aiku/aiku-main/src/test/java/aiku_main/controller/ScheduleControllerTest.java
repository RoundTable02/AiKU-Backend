package aiku_main.controller;

import aiku_main.dto.*;
import aiku_main.dto.schedule.ScheduleAddDto;
import aiku_main.dto.schedule.ScheduleUpdateDto;
import aiku_main.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        ScheduleAddDto scheduleDto = new ScheduleAddDto(
                "schedule",
                new LocationDto("lo1", 1.0, 1.0),
                LocalDateTime.now().plusHours(1));

        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addScheduleWithFaultDto() throws Exception {
        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        " ",
                                        new LocationDto("loc1", 1.1, 1.1),
                                        LocalDateTime.now().plusHours(1))
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        "schedule name is too long",
                                        new LocationDto("loc1", 1.1, 1.1),
                                        LocalDateTime.now().plusHours(1))
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        "name1",
                                        new LocationDto(" ", 1.1, 1.1),
                                        LocalDateTime.now().plusHours(1))
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        "name1",
                                        new LocationDto("lo1", 1.1, 1.1),
                                        null)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        "name1",
                                        new LocationDto("lo1", null, 1.1),
                                        LocalDateTime.now().plusHours(1))
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addScheduleWithFaultScheduleTime() throws Exception {
        mockMvc.perform(post("/groups/1/schedules")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ScheduleAddDto(
                                        "name1",
                                        new LocationDto("lo1", null, 1.1),
                                        LocalDateTime.now())
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto("sche1",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now().plusHours(1));
        mockMvc.perform(patch("/groups/1/schedules/1")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateScheduleWithFaultDto() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto(" ",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now().plusHours(1));
        mockMvc.perform(patch("/groups/1/schedules/1")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateScheduleWithFaultScheduleTime() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto(" ",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now().plusMinutes(30));
        mockMvc.perform(patch("/groups/1/schedules/1")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enterSchedule() throws Exception {
        mockMvc.perform(post("/groups/1/schedules/1/enter")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ScheduleEnterDto(0))))
                .andExpect(status().isOk());
    }

    @Test
    void enterScheduleWithFaultPoint() throws Exception {
        mockMvc.perform(post("/groups/1/schedules/1/enter")
                        .header("Access-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ScheduleEnterDto(3))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getScheduleDates() throws Exception {
        mockMvc.perform(get("/member/schedules/dates")
                        .header("Access-Member-Id", 1L)
                        .param("year", "2025")
                        .param("month", "11"))
                .andExpect(status().isOk());
    }

    @Test
    void getScheduleDatesWithFaultDate() throws Exception {
        mockMvc.perform(get("/member/schedules/dates")
                        .header("Access-Member-Id", 1L)
                        .param("year", "2024")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());
    }
}