package aiku_main.service;

import aiku_main.dto.racing.ScheduleRacing;
import aiku_main.dto.racing.ScheduleRacingMember;
import aiku_main.dto.racing.ScheduleRacingResult;
import aiku_main.exception.BettingException;
import aiku_main.repository.RacingQueryRepository;
import aiku_main.repository.ScheduleQueryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.Racing;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static common.response.status.BaseErrorCode.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RacingService {

    private final RacingQueryRepository racingQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public void analyzeScheduleRacingResult(Long scheduleId) {
        List<Racing> racings = racingQueryRepository.findTermRacingsInSchedule(scheduleId);

        if (racings.isEmpty()) {
            return;
        }

        Map<Long, ScheduleMember> scheduleMembers =
                scheduleQueryRepository.findScheduleMembersWithMember(scheduleId).stream()
                        .collect(Collectors.toMap(
                                sm -> sm.getId(),
                                sm -> sm
                        ));

        List<ScheduleRacing> racingDtoList = racings.stream()
                .map(racing -> {
                    ScheduleRacingMember firstRacer = new ScheduleRacingMember(scheduleMembers.get(racing.getFirstRacer().getId()).getMember());
                    ScheduleRacingMember secondRacer = new ScheduleRacingMember(scheduleMembers.get(racing.getSecondRacer().getId()).getMember());
                    return new ScheduleRacing(firstRacer, secondRacer, racing.getPointAmount(), racing.getWinner().getId());
                }).toList();

        ScheduleRacingResult result = new ScheduleRacingResult(scheduleId, racingDtoList);

        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        try {
            schedule.setScheduleRacingResult(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new BettingException(INTERNAL_SERVER_ERROR, "Can't Parse ScheduleRacingResult");
        }
    }
}
