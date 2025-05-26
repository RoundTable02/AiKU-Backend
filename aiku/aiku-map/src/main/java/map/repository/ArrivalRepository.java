package map.repository;

import common.domain.Arrival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArrivalRepository extends JpaRepository<Arrival, Long>, ArrivalQueryRepository {
}
