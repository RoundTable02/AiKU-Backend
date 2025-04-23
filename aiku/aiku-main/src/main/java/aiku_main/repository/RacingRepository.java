package aiku_main.repository;

import common.domain.Racing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingRepository extends JpaRepository<Racing, Long>, RacingRepositoryCustom {
}
