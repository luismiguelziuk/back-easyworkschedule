package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.cex.creation.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristInformerUpdateDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointUpdateDTO;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface CexService {
    TouristPoint createTouristPoint(TouristPointCreationDTO touristPointDTO);
    TouristInformer createTouristInformer(TouristInformerCreationDTO touristInformerDTO);
    Team createTeam(String name);
    TouristPoint updateTouristPoint(String name, TouristPointUpdateDTO touristPointDTO);
    TouristInformer updateTouristInformer(String name, TouristInformerUpdateDTO touristInformerDTO);
    void checkOrCreateBreakPoint();
}
