package aiku_main.repository;

import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BettingRepository extends JpaRepository<Betting, Long>, BettingRepositoryCustom {
    boolean existsByIdAndBettorAndBettingStatusAndStatus(Long memberId, Long scheduleId, ExecStatus bettingStatus, Status status);
}
