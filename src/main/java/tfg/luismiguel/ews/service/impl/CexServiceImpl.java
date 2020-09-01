package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.cex.creation.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristInformerUpdateDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointUpdateDTO;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.repository.cex.TeamRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.CexService;

import java.util.ArrayList;
import java.util.List;
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
    public TouristPoint createTouristPoint(TouristPointCreationDTO touristPointDTO) {
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
    public TouristInformer createTouristInformer(TouristInformerCreationDTO touristInformerDTO) {
        TouristInformer touristInformer = new TouristInformer();
        touristInformer.setName(touristInformerDTO.getName());
        touristInformer.setWorkHours(touristInformerDTO.getWorkHours());
        touristInformer.setTeam(teamRepository.findById(touristInformerDTO.getTeam().getId()).get());
        touristInformerRepository.save(touristInformer);
        return touristInformer;
    }

    @Override
    public Team createTeam(String name) {
        Team team = new Team();
        team.setName(name);
        teamRepository.save(team);
        return team;
    }

    @Override
    public TouristPoint updateTouristPoint(String name, TouristPointUpdateDTO touristPointDTO) {
        TouristPoint touristPointToUpdate = touristPointRepository.findAll().stream()
                .filter(touristPoint -> touristPoint.getName().equals(name))
                .findFirst().get();
        if (touristPointDTO.getName() != null && !touristPointDTO.getName().isEmpty()) {
            touristPointToUpdate.setName(touristPointDTO.getName());
        }
        if (touristPointDTO.getCloseTime() != null) {
            touristPointToUpdate.setCloseTime(touristPointDTO.getCloseTime());
        }
        if (touristPointDTO.getOpenTime() != null) {
            touristPointToUpdate.setOpenTime(touristPointDTO.getOpenTime());
        }
        if (touristPointDTO.getPriority() != null) {
            touristPointToUpdate.setPriority(touristPointDTO.getPriority());
        }
        if (touristPointDTO.getDismissDate() != null) {
            touristPointToUpdate.setDismissDate(touristPointDTO.getDismissDate());
        }
        if (touristPointDTO.getTrainedTeams() != null) {
            touristPointToUpdate.setTrainedTeams(touristPointDTO.getTrainedTeams().stream()
                    .map(teamDTO -> teamRepository.findById(teamDTO.getId()).get())
                    .collect(Collectors.toList()));
        }
        touristPointRepository.save(touristPointToUpdate);
        return touristPointToUpdate;
    }

    @Override
    public TouristInformer updateTouristInformer(String name, TouristInformerUpdateDTO touristInformerDTO) {
        TouristInformer touristInformerToUpdate = touristInformerRepository.findAll().stream()
                .filter(touristPoint -> touristPoint.getName().equals(name))
                .findFirst().get();
        if (touristInformerDTO.getName() != null && !touristInformerDTO.getName().isEmpty()) {
            touristInformerToUpdate.setName(touristInformerDTO.getName());
        }
        if (touristInformerDTO.getWorkHours() != null) {
            touristInformerToUpdate.setWorkHours(touristInformerDTO.getWorkHours());
        }
        if (touristInformerDTO.getTeam() != null) {
            touristInformerToUpdate.setTeam(teamRepository.findById(touristInformerDTO.getTeam().getId()).get());
        }
        if (touristInformerDTO.getDismissDate() != null) {
            touristInformerToUpdate.setDismissDate(touristInformerDTO.getDismissDate());
        }
        touristInformerRepository.save(touristInformerToUpdate);
        return touristInformerToUpdate;
    }

    @Override
    public void checkOrCreateBreakPoint() {
        boolean descanso;
        boolean descansoAleatorio;
        List<TouristPoint> breakPoints = touristPointRepository.findAll().stream()
                .filter(touristPoint -> touristPoint.getName().equals("Descanso")
                        || touristPoint.getName().equals("Descanso Aleatorio")).collect(Collectors.toList());
        descanso = breakPoints.stream().anyMatch(touristPoint -> touristPoint.getName().equals("Descanso"));
        descansoAleatorio = breakPoints.stream().anyMatch(touristPoint -> touristPoint.getName().equals("Descanso Aleatorio"));
        if (!descanso) {
            TouristPoint touristPoint = new TouristPoint();
            touristPoint.setName("Descanso");
            touristPoint.setTrainedTeams(new ArrayList<>());
            touristPoint.setPriority(5.0);
            touristPoint.setOpenTime(8.00);
            touristPoint.setCloseTime(8.00);
            touristPointRepository.save(touristPoint);
        }
        if (!descansoAleatorio) {
            TouristPoint touristPoint = new TouristPoint();
            touristPoint.setName("Descanso Aleatorio");
            touristPoint.setTrainedTeams(new ArrayList<>());
            touristPoint.setPriority(5.0);
            touristPoint.setOpenTime(8.00);
            touristPoint.setCloseTime(8.00);
            touristPointRepository.save(touristPoint);
        }
    }
}
