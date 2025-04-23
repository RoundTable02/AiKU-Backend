package aiku_main.repository.racing;

import common.domain.Racing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingRepository extends JpaRepository<Racing, Long>, RacingRepositoryCustom {
}
