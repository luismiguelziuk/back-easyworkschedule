package tfg.luismiguel.ews.repository.cex;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
