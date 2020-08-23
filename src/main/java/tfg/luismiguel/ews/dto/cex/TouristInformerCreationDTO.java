package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.cex.Team;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristInformerCreationDTO {
    private Team team;
    private String name;
    private Double workHours;
}
