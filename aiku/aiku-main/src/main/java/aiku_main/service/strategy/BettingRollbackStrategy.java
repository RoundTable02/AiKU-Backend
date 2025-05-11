package aiku_main.service.strategy;


import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.betting.BettingRepository;
import common.domain.Status;
import common.domain.betting.Betting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static aiku_main.application_event.event.PointChangeReason.*;

@PointChangeReasonMapping({BETTING, BETTING_CANCLE, BETTING_REWARD})
@RequiredArgsConstructor
@Transactional
@Service
public class BettingRollbackStrategy implements RollbackStrategy{

    private final BettingRepository bettingRepository;

    @Override
    public void execute(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        Betting betting = bettingRepository.findById(reasonId).orElseThrow();
        betting.setStatus(Status.ERROR);
    }
}
