package tfg.luismiguel.ews.repository.cex;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface TouristPointRepository extends JpaRepository<TouristPoint, Long> {
}
