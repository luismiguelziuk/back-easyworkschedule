package tfg.luismiguel.ews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.entity.cex.TouristInformer;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeekKnappSackDTO {
    WeekDTO week;
    Map<TouristInformerDTO, Double> availableWorkersHours;
    Double health;
}
