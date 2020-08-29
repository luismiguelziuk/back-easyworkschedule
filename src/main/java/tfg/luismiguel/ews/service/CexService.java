package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface CexService {
    TouristPoint createTouristPoint(TouristPointDTO touristPointDTO);
    TouristInformer createTouristInformer(TouristInformerDTO touristInformerDTO);
    Team createTeam(String name);
}
