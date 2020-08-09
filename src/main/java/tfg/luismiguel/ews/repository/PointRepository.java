package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
}
