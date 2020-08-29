package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.cex.TouristInformer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TouristInformerDTO {
    private Long id;
    private TeamDTO team;
    private String name;
    private Double workHours;
    private Double accumulatedHours;

    public TouristInformerDTO(TouristInformer touristInformer) {
        this.id = touristInformer.getId();
        this.team = new TeamDTO(touristInformer.getTeam());
        this.name = touristInformer.getName();
        this.workHours = touristInformer.getWorkHours();
        this.accumulatedHours = touristInformer.getAccumulatedHours();
    }
}
