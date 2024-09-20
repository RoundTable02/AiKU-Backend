package aiku_main.repository;

import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BettingRepository extends JpaRepository<Betting, Long>, BettingRepositoryCustom {
    Optional<Betting> findByBettorScheduleMemberIdAndStatus(Long bettorScheduleMemberId, Status status);
    Optional<Betting> findByBeteeScheduleMemberIdAndStatus(Long beteeScheduleMemberId, Status status);
    boolean existsByIdAndBettorAndBettingStatusAndStatus(Long memberId, Long scheduleId, ExecStatus bettingStatus, Status status);
}
