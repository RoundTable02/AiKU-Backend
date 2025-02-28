package aiku_main.service.strategy;


import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.BettingQueryRepository;
import common.domain.Betting;
import common.domain.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PointChangeReasonMapping(PointChangeReason.BETTING)
@RequiredArgsConstructor
@Transactional
@Service
public class BettingRollbackStrategy implements RollbackStrategy{

    private final BettingQueryRepository bettingRepository;

    @Override
    public void execute(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        Betting betting = bettingRepository.findById(reasonId).orElseThrow();
        betting.setStatus(Status.ERROR);
    }
}
