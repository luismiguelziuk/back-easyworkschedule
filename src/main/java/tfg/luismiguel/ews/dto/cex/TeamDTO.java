package tfg.luismiguel.ews.dto.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.entity.cex.Team;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
    }
}
