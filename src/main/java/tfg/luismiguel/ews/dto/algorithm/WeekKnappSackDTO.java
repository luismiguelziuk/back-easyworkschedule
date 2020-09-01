package tfg.luismiguel.ews.dto.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.WeekDTO;
import tfg.luismiguel.ews.dto.algorithm.cex.TouristPointDayProblemDTO;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeekKnappSackDTO {
    WeekDTO week;
    Map<TouristInformerDTO, Double> availableWorkersHours;
    Double health;
    List<TouristPointDayProblemDTO> errors;
}
