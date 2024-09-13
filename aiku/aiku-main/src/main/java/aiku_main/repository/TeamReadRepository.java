package aiku_main.repository;

import common.domain.Team;

import java.util.Optional;

public interface TeamReadRepository {
    Optional<Team> findTeamWithMember(Long teamId);
}
