package tfg.luismiguel.ews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.Day;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DayDTO {
    private Long id;
    private DayOfWeek dayOfWeek;
    private List<ShiftDTO> shifts;

    public DayDTO(Day day) {
        this.id = day.getId();
        this.dayOfWeek = day.getDayOfWeek();
        this.shifts = day.getShifts().stream().map(ShiftDTO::new).collect(Collectors.toList());

    }
}
