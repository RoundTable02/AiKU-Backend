package aiku_main.repository;

import common.domain.Status;
import common.domain.team.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamQueryRepository extends JpaRepository<Team, Long>, TeamQueryRepositoryCustom {

    Optional<Team> findByIdAndStatus(Long teamId, Status status);
}
