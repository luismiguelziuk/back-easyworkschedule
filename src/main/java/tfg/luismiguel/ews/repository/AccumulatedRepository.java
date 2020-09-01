package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.AccumulatedHours;
import tfg.luismiguel.ews.entity.Worker;

public interface AccumulatedRepository extends JpaRepository<AccumulatedHours, Long> {
}
