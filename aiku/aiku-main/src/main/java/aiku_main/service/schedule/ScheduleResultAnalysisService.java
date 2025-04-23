package aiku_main.service.schedule;

import aiku_main.dto.schedule.ScheduleArrivalMember;
import aiku_main.dto.schedule.ScheduleArrivalResult;
import aiku_main.repository.schedule.ScheduleRepository;
import common.domain.schedule.Schedule;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ScheduleResultAnalysisService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void analyzeScheduleArrivalResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleWithResult(scheduleId).orElseThrow();

        List<ScheduleArrivalMember> arrivalMembers = scheduleRepository.getScheduleArrivalResults(scheduleId);
        ScheduleArrivalResult arrivalResult = new ScheduleArrivalResult(scheduleId, arrivalMembers);
        schedule.setScheduleArrivalResult(ObjectMapperUtil.toJson(arrivalResult));
    }
}
