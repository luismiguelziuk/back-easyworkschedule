package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristPointDTO {
    private Long id;
    private Double priority;
    private String name;
    private Double openTime;
    private Double closeTime;
    private List<TeamDTO> trainedTeams;
    private LocalDate dismissDate;

    public TouristPointDTO(TouristPoint touristPoint) {
        this.id = touristPoint.getId();
        this.priority = touristPoint.getPriority();
        this.name = touristPoint.getName();
        this.openTime = touristPoint.getOpenTime();
        this.closeTime = touristPoint.getCloseTime();
        this.trainedTeams = touristPoint.getTrainedTeams().stream()
                .map(TeamDTO::new).collect(Collectors.toList());
        this.dismissDate = touristPoint.getDismissDate();
    }

    public Double getTime() {
        if (getOpenTime() <= 13 && getCloseTime() >= 16) {
            return getCloseTime() - getOpenTime() - 1;
        } else {
            return getCloseTime() - getOpenTime();
        }
    }
}
