package tfg.luismiguel.ews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.Week;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder=true)
public class WeekDTO {
    private Long id;
    private Long numberOfWeek;
    private Long year;
    private List<DayDTO> days;

    public WeekDTO(Week week) {
        this.id = week.getId();
        this.numberOfWeek =week.getNumberOfWeek();
        this.year = week.getYear();
        this.days = week.getDays().stream().map(DayDTO::new).collect(Collectors.toList());
    }
}
