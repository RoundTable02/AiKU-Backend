package aiku_main.repository.term;

import common.domain.term.Term;
import common.domain.term.TermTitle;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    @Query("SELECT t FROM Term t WHERE t.termTitle = :termTitle ORDER BY t.version DESC LIMIT 1")
    Optional<Term> findTopByTermTitleOrderByVersionDesc(@Param("termTitle") TermTitle termTitle);
}
