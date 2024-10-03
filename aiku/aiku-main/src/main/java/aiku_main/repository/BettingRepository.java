package aiku_main.repository;

import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BettingRepository extends JpaRepository<Betting, Long>, BettingRepositoryCustom {

    Optional<Betting> findByBettorIdAndStatus(@Param(value = "bettorId") Long bettorScheduleMemberId, Status status);
    Optional<Betting> findByBeteeIdAndStatus(@Param(value = "beteeId") Long beteeScheduleMemberId, Status status);
    boolean existsByIdAndBettorAndBettingStatusAndStatus(Long memberId, Long scheduleId, ExecStatus bettingStatus, Status status);
}
