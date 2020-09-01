package tfg.luismiguel.ews.service;

import tfg.luismiguel.ews.dto.excel.GenerateExcelDTO;
import tfg.luismiguel.ews.exception.EwsException;

import java.io.IOException;

public interface ExcelService {
    byte[] generateExcel(GenerateExcelDTO generateExcelDTO) throws EwsException, IOException;
}
