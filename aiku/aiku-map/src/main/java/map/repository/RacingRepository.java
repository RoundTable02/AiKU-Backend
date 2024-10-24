package map.repository;

import common.domain.ExecStatus;
import common.domain.Racing;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingRepository extends JpaRepository<Racing, Long>, RacingQueryRepository {

    boolean existsByIdAndRaceStatusAndStatus(Long racingId, ExecStatus scheduleStatus, Status status);
}
