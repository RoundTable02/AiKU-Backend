package map.scheduler;

import common.domain.ExecStatus;
import common.domain.racing.Racing;
import lombok.RequiredArgsConstructor;
import map.application_event.domain.RacingInfo;
import map.application_event.publisher.RacingEventPublisher;
import map.exception.RacingException;
import map.repository.RacingQueryRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static common.response.status.BaseErrorCode.NO_SUCH_RACING;


@RequiredArgsConstructor
@Component
public class RacingScheduler {

    private final TaskScheduler taskScheduler;
    private final RacingEventPublisher racingEventPublisher;
    private final RacingQueryRepository racingRepository;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> racingTaskMap = new ConcurrentHashMap<>();

    // Racing 생성 후 30초 타이머 설정
    public void checkRacingStatus30secsLater(RacingInfo racingInfo) {
        // 30초 후 작업 예약
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> checkRacingStatus(racingInfo),
                new java.util.Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30))
        );

        // ConcurrentHashMap에 저장
        racingTaskMap.put(racingInfo.getRacingId(), future);
    }

    // 30초 후 Racing의 상태를 확인하고 WAIT이면 삭제
    private void checkRacingStatus(RacingInfo racingInfo) {
        Racing racing = racingRepository.findById(racingInfo.getRacingId())
                .orElseThrow(() -> new RacingException(NO_SUCH_RACING));

        if (racing.getRaceStatus().equals(ExecStatus.WAIT)) {
            racingEventPublisher.publishRacingStatusNotChangedEvent(racingInfo);
        }
    }
}
