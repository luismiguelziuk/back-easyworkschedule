package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.WeekCreationDTO;
import tfg.luismiguel.ews.entity.Week;

public interface TemporalService {
    Week createWeek(WeekCreationDTO weekCreationDTO);
}
