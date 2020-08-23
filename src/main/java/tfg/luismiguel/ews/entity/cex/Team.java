package tfg.luismiguel.ews.entity.cex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "team")
public enum Team {
    VISITOR (0L, "Visitor"),
    ADVANCE(1L, "Advance"),
    MEDIUM(2L, "Medium"),
    MUMS(3L, "Mums and Dads"),
    ROOKIE(4L, "Rookies");
    @Id
    private Long id;
    private String description;
}
