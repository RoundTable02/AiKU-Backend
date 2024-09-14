package aiku_main.repository;

import common.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamReadRepository extends JpaRepository<Team, Long>, TeamReadRepositoryCustom {
}
