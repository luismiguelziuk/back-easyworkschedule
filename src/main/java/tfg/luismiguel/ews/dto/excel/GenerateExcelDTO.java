package tfg.luismiguel.ews.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GenerateExcelDTO {
    private Long year;
    private List<Long> weeks;
}
