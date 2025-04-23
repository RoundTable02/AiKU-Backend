package aiku_main.repository.team;

import common.domain.Status;
import common.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

    Optional<Team> findByIdAndStatus(Long teamId, Status status);
}
