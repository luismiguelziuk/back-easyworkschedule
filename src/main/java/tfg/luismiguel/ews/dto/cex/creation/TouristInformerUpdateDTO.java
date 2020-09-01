package tfg.luismiguel.ews.dto.cex.creation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.cex.TeamDTO;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristInformerUpdateDTO {
    private TeamDTO team;
    private String name;
    private Double workHours;
    private Double accumulatedHours;
    private LocalDate dismissDate;
}
