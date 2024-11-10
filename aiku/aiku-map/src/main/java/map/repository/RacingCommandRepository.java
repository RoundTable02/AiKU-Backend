package map.repository;

import common.domain.ExecStatus;
import common.domain.Racing;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingCommandRepository extends JpaRepository<Racing, Long>, RacingCommandRepositoryCustom {

}
