package aiku_main.repository;

import common.domain.Betting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BettingRepository extends JpaRepository<Betting, Long> {
}
