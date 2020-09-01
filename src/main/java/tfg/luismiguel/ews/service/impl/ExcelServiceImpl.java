package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.excel.GenerateExcelDTO;
import tfg.luismiguel.ews.entity.Day;
import tfg.luismiguel.ews.entity.Shift;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.repository.WeekRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.service.ExcelService;
import tfg.luismiguel.ews.service.TemporalService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private TouristInformerRepository touristInformerRepository;
    @Autowired
    private TemporalService temporalService;

    @Override
    public byte[] generateExcel(GenerateExcelDTO generateExcelDTO) throws EwsException, IOException {
        HSSFWorkbook book = new HSSFWorkbook();
        for (Long weekId : generateExcelDTO.getWeeks()) {
            int rowToCreated = 0;
            Map<Long, HSSFRow> mapInformerIdRowId = new HashMap<>();
            HSSFSheet sheet = book.createSheet("Semana " + weekId);
            Week week = temporalService.findWeekByNumberAndYear(weekId, generateExcelDTO.getYear());
            List<TouristInformer> allTouristInformerOrderByTeam = touristInformerRepository.findAll().stream()
                    .filter(touristInformer -> touristInformer.getDismissDate() == null)
                    .sorted(Comparator.comparing(touristInformer -> touristInformer.getTeam().getId()))
                    .collect(Collectors.toList());
            HSSFRow cabecera = sheet.createRow(rowToCreated);
            HSSFCell tituloTnformador = cabecera.createCell(0, CellType.STRING);
            tituloTnformador.setCellValue("Informador");
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                HSSFCell cell = cabecera.createCell(dayOfWeek.getValue(), CellType.STRING);
                cell.setCellValue(dayOfWeek.name());
            }
            rowToCreated++;
            for (TouristInformer touristInformer : allTouristInformerOrderByTeam) {
                HSSFRow row = sheet.createRow(rowToCreated);
                mapInformerIdRowId.put(touristInformer.getId(), row);
                HSSFCell cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(touristInformer.getName());
                rowToCreated++;
            }
            for (Day day : week.getDays()) {
                for (Shift shift : day.getShifts()) {
                    mapInformerIdRowId.get(shift.getWorker().getId())
                            .createCell(day.getDayOfWeek().getValue(), CellType.STRING)
                            .setCellValue(shift.getPoint().getName());
                }
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            book.write(bos);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }
}
