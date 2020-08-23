package tfg.luismiguel.ews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeekCreationDTO {
    private DayOfWeek dayOfWeekStart;
    private Long numberOfWeek;
    private Long year;
}
