package tfg.luismiguel.ews.dto.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FillWeekDTO {
    Long numberOfWeek;
    Long year;
    int numberIteration;
    int numberOfMutation;
}
