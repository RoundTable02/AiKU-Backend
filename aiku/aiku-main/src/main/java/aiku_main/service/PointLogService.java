package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.PointLogRepository;
import common.domain.log.PointLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PointLogService {

    private final PointLogRepository pointLogRepository;
    private final PointLogFactory pointLogFactory;

    @Transactional
    public void savePointLog(PointChangeReason pointChangeReason, Long memberId, PointChangeType pointChangeType, int pointAmount, Long reasonId) {
        PointLog pointLog = pointLogFactory.createPointLog(pointChangeReason,
                memberId,
                pointChangeType,
                pointAmount,
                reasonId);

        pointLogRepository.save(pointLog);
    }
}
