package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.WeekDTO;
import tfg.luismiguel.ews.entity.Week;

public interface TemporalService {
    Week createWeek(WeekDTO weekDTO);
}
