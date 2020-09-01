package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.creation.WeekCreationDTO;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.exception.EwsException;

public interface TemporalService {
    Week createWeek(WeekCreationDTO weekDTO);
    Week findWeekByNumberAndYear(Long numberOfWeek, Long year) throws EwsException;
}
