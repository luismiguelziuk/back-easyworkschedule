package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.Week;

public interface WeekRepository extends JpaRepository<Week, Long> {
}
