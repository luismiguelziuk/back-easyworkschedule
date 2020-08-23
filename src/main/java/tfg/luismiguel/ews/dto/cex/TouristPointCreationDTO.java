package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.cex.Team;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristPointCreationDTO {
    private Double priority;
    private String name;
    private Double openTime;
    private Double closeTime;
    private List<Team> trainedTeams;
}
