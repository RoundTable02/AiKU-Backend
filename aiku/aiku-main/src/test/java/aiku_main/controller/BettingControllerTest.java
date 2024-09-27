package aiku_main.controller;

import aiku_main.dto.BettingAddDto;
import aiku_main.service.BettingService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BettingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    BettingService bettingService;
    @InjectMocks
    BettingController bettingController;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(bettingController)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addBetting() throws Exception {
        mockMvc.perform(post("/schedules/1/bettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BettingAddDto(1L, 100)
                        )))
                .andExpect(status().isOk());
    }

    @Test
    void addBettingWithFaultDto() throws Exception {
        mockMvc.perform(post("/schedules/1/bettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BettingAddDto(null, 100)
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBettingWithFaultPoint() throws Exception {
        mockMvc.perform(post("/schedules/1/bettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BettingAddDto(1L, 0)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/schedules/1/bettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BettingAddDto(1L, 101)
                        )))
                .andExpect(status().isBadRequest());
    }
}