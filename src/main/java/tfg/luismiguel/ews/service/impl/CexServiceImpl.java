package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.repository.cex.TeamRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.CexService;

import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Transactional
public class CexServiceImpl implements CexService {
    @Autowired
    private TouristPointRepository touristPointRepository;
    @Autowired
    private TouristInformerRepository touristInformerRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Override
    public TouristPoint createTouristPoint(TouristPointDTO touristPointDTO) {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setName(touristPointDTO.getName());
        touristPoint.setOpenTime(touristPointDTO.getOpenTime());
        touristPoint.setCloseTime(touristPointDTO.getCloseTime());
        touristPoint.setPriority(touristPointDTO.getPriority());
        touristPoint.setTrainedTeams(touristPointDTO.getTrainedTeams().stream()
                                        .map(teamDTO -> teamRepository.findById(teamDTO.getId()).get())
                                        .collect(Collectors.toList()));
        touristPointRepository.save(touristPoint);
        return touristPoint;
    }

    @Override
    public TouristInformer createTouristInformer(TouristInformerDTO touristInformerDTO) {
        TouristInformer touristInformer = new TouristInformer();
        touristInformer.setName(touristInformerDTO.getName());
        touristInformer.setWorkHours(touristInformerDTO.getWorkHours());
        touristInformer.setAccumulatedHours(0.0);
        touristInformer.setTeam(teamRepository.findById(touristInformerDTO.getTeam().getId()).get());
        touristInformerRepository.save(touristInformer);
        return touristInformer;
    }

    @Override
    public Team createTeam(String name) {
        Team team =  new Team();
        team.setName(name);
        teamRepository.save(team);
        return team;
    }
}
