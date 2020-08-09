package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
