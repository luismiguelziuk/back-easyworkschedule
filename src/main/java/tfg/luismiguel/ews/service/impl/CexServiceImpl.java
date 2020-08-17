package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.entity.cex.Team;
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
    public TouristPoint createTouristPoint(String name, Double openTime, Double closeTime, Long priority) {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setName(name);
        touristPoint.setOpenTime(openTime);
        touristPoint.setCloseTime(closeTime);
        touristPoint.setPriority(priority);
        touristPointRepository.save(touristPoint);
        return touristPoint;
    }

    @Override
    public TouristInformer createTouristInformer(String name, Double hours, Team team ) {
        TouristInformer touristInformer = new TouristInformer();
        touristInformer.setName(name);
        touristInformer.setWorkHours(hours);
        touristInformer.setAccumulatedHours(0.0);
        touristInformer.setTeam(team);
        touristInformerRepository.save(touristInformer);
        return touristInformer;
    }
}
