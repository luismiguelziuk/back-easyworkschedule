package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.AccumulatedHour;

public interface AccumulatedRepository extends JpaRepository<AccumulatedHour, Long> {
}
