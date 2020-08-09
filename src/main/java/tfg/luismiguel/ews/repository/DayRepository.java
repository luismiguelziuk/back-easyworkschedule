package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.Day;

public interface DayRepository extends JpaRepository<Day, Long> {
}
