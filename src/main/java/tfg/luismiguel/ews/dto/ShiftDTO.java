package tfg.luismiguel.ews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.entity.Shift;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShiftDTO {
    private Long id;
    private TouristInformerDTO worker;
    private TouristPointDTO point;

    public ShiftDTO(Shift shift) {
        this.id = shift.getId();
        this.worker = new TouristInformerDTO((TouristInformer) shift.getWorker());
        this.point = new TouristPointDTO((TouristPoint) shift.getPoint());
    }
}
