package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.algorithm.CleanWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.FillWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.WeekKnappSackDTO;
import tfg.luismiguel.ews.exception.EwsException;

public interface AlgorithmService {
    void saveAll(WeekKnappSackDTO weekKnappSackDTO, FillWeekDTO fillWeekDTO) throws EwsException;
    WeekKnappSackDTO fillWeekRecursive(FillWeekDTO fillWeekDTO) throws EwsException;
    void cleanWeek(CleanWeekDTO cleanWeekDTO) throws EwsException;
}
