package aiku_main.scheduler;

import aiku_main.repository.ScheduleRepository;
import common.domain.ExecStatus;
import common.domain.Schedule;
import common.exception.BaseExceptionImpl;
import common.response.status.BaseErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@RequiredArgsConstructor
@Component
public class ScheduleScheduler {

    private final TaskScheduler scheduler;
    private final ScheduleRepository scheduleRepository;

    private final ConcurrentHashMap<Long, ScheduledFuture> mapOpenTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture> mapCloseTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture> scheduleAlarmTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void initScheduler(){
        List<Schedule> schedules = scheduleRepository.findByScheduleStatus(ExecStatus.WAIT);
        schedules.forEach(
                schedule -> reserveSchedule(schedule.getId(), schedule.getScheduleTime()));
    }

    public void reserveSchedule(Long scheduleId, LocalDateTime scheduleTime){
        reserveMapOpen(scheduleId, scheduleTime);
        reserveMapClose(scheduleId, scheduleTime);
        reserveAlarm(scheduleId, scheduleTime);
    }

    public void changeSchedule(Long scheduleId, LocalDateTime scheduleTime){
        cancleAll(scheduleId);
        reserveSchedule(scheduleId, scheduleTime);
    }

    private void reserveAlarm(Long scheduleId, LocalDateTime scheduleTime){
        Duration delayTime = getDuration(scheduleTime);
        checkDurationValid(delayTime);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(null, delayTime);
        scheduleAlarmTasks.put(scheduleId, future);
    }

    private void reserveMapOpen(Long scheduleId, LocalDateTime scheduleTime){
        Duration delayTime = getDuration(scheduleTime).minus(Duration.ofMinutes(30));
        checkDurationValid(delayTime);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(null, delayTime);
        mapOpenTasks.put(scheduleId, future);
    }

    private void reserveMapClose(Long scheduleId, LocalDateTime scheduleTime){
        Duration delayTime = getDuration(scheduleTime).plus(Duration.ofMinutes(30));
        checkDurationValid(delayTime);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(null, delayTime);
        mapCloseTasks.put(scheduleId, future);
    }

    private void cancleAll(Long scheduleId){
        ScheduledFuture future1 = mapOpenTasks.get(scheduleId);
        future1.cancel(false);
        mapOpenTasks.remove(scheduleId);

        ScheduledFuture future2 = mapCloseTasks.get(scheduleId);
        future2.cancel(false);
        mapCloseTasks.remove(scheduleId);

        ScheduledFuture future3 = scheduleAlarmTasks.get(scheduleId);
        future3.cancel(false);
        scheduleAlarmTasks.remove(scheduleId);
    }

    private Duration getDuration(LocalDateTime time){
        return Duration.between(LocalDateTime.now(), time);
    }

    private void checkDurationValid(Duration duration){
        if(duration.toMinutes() < 0){
            throw new BaseExceptionImpl(BaseErrorCode.NO_VALID_SCHEDULE_TIME);
        }
    }
}
