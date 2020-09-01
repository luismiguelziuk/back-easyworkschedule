package tfg.luismiguel.ews.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder=true)
@Entity
@Table(name = "week", uniqueConstraints = @UniqueConstraint(columnNames = {"numberOfWeek", "year"}))
public class Week {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private Long numberOfWeek;
    @NotBlank
    private Long year;
    @OneToMany
    private List<Day> days;
    @OneToMany
    List<AccumulatedHours> accumulatedHours;
}
