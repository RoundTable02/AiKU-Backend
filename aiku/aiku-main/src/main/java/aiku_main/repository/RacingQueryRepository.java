package aiku_main.repository;

import common.domain.Racing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacingQueryRepository extends JpaRepository<Racing, Long>, RacingQueryRepositoryCustom {
}
