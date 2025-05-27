package map.repository.arrival;

import common.domain.Arrival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalRepository extends JpaRepository<Arrival, Long>, ArrivalRepositoryCustom {
}
