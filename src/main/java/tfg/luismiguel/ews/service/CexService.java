package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.cex.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointCreationDTO;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

public interface CexService {
    TouristPoint createTouristPoint(TouristPointCreationDTO touristPointDTO);
    TouristInformer createTouristInformer(TouristInformerCreationDTO touristInformerDTO);
}
