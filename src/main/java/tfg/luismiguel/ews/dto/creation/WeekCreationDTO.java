package tfg.luismiguel.ews.dto.creation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeekCreationDTO {
    private Long numberOfWeek;
    private Long year;
}
