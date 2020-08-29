package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.FillWeekDTO;
import tfg.luismiguel.ews.exception.EwsException;

public interface AlgorithmService {
    void fillCompleteWeek(FillWeekDTO fillWeekDTO) throws EwsException;
}
