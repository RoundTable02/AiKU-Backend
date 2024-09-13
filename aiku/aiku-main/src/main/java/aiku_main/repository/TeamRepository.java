package aiku_main.repository;

import common.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT CASE WHEN COUNT(tm) > 0 THEN true ELSE false END " +
            "FROM TeamMember tm WHERE tm.member.id = :memberId " +
            "AND tm.team.id = :teamId AND tm.status = 'ALIVE'")
    boolean existTeamMember(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
}
