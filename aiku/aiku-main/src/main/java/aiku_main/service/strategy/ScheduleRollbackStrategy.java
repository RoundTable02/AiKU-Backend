package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.ScheduleQueryRepository;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static aiku_main.application_event.event.PointChangeReason.SCHEDULE_EXIT;
import static aiku_main.application_event.event.PointChangeReason.SCHEDULE_REWARD;

@PointChangeReasonMapping({SCHEDULE_EXIT, SCHEDULE_REWARD})
@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleRollbackStrategy implements RollbackStrategy{

    private final ScheduleQueryRepository scheduleRepository;

    @Override
    public void execute(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        ScheduleMember scheduleMember = scheduleRepository.findScheduleMemberWithSchedule(memberId, reasonId).orElseThrow();

        Schedule schedule = scheduleMember.getSchedule();
        schedule.errorScheduleMember(scheduleMember);
    }
}
