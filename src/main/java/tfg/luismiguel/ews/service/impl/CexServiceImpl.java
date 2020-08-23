package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.cex.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointCreationDTO;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.CexService;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Transactional
public class CexServiceImpl implements CexService {
    @Autowired
    private TouristPointRepository touristPointRepository;
    @Autowired
    private TouristInformerRepository touristInformerRepository;

    @Override
    public TouristPoint createTouristPoint(TouristPointCreationDTO touristPointDTO) {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setName(touristPointDTO.getName());
        touristPoint.setOpenTime(touristPointDTO.getOpenTime());
        touristPoint.setCloseTime(touristPointDTO.getCloseTime());
        touristPoint.setPriority(touristPointDTO.getPriority());
        touristPoint.setTrainedTeams(touristPointDTO.getTrainedTeams());
        touristPointRepository.save(touristPoint);
        return touristPoint;
    }

    @Override
    public TouristInformer createTouristInformer(TouristInformerCreationDTO touristInformerDTO) {
        TouristInformer touristInformer = new TouristInformer();
        touristInformer.setName(touristInformerDTO.getName());
        touristInformer.setWorkHours(touristInformerDTO.getWorkHours());
        touristInformer.setAccumulatedHours(0.0);
        touristInformer.setTeam(touristInformerDTO.getTeam());
        touristInformerRepository.save(touristInformer);
        return touristInformer;
    }
}
