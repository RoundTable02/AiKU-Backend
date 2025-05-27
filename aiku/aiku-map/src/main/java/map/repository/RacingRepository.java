package map.repository;

import common.domain.ExecStatus;
import common.domain.Status;
import common.domain.racing.Racing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingRepository extends JpaRepository<Racing, Long>, RacingRepositoryCustom {

    boolean existsByIdAndRaceStatusAndStatus(Long racingId, ExecStatus scheduleStatus, Status status);

}
