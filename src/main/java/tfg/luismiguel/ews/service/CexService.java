package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface CexService {
    TouristPoint createTouristPoint(String name, Double openTime, Double closeTime, Long priority);
    TouristInformer createTouristInformer(String name, Double hours, Team team);
}
