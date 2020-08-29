package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.DayDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristPointDayProblemDTO {
    private TouristPointDTO touristPoint;
    private DayDTO day;
}
