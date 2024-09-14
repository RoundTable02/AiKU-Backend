package aiku_main.repository;

import common.domain.Team;

import java.util.Optional;

public interface TeamReadRepositoryCustom {
    Optional<Team> findTeamWithMember(Long teamId);
}
