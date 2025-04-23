package map.repository;

import common.domain.ExecStatus;
import common.domain.racing.Racing;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingQueryRepository extends JpaRepository<Racing, Long>, RacingQueryRepositoryCustom {

    boolean existsByIdAndRaceStatusAndStatus(Long racingId, ExecStatus scheduleStatus, Status status);
}
