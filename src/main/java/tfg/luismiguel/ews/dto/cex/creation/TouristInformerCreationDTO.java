package tfg.luismiguel.ews.dto.cex.creation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.cex.TeamDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristInformerCreationDTO {
    private TeamDTO team;
    private String name;
    private Double workHours;
}
