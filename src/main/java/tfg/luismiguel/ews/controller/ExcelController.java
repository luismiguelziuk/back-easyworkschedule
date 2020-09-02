package tfg.luismiguel.ews.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfg.luismiguel.ews.dto.excel.GenerateExcelDTO;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.service.ExcelService;

import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    @Autowired
    ExcelService excelService;

    @Operation(summary = "Generate a excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel was generated"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Excel not generated",
                    content = @Content)})
    @PutMapping("/generate/excel")
    public ResponseEntity<byte[]> generateExcel(@RequestBody GenerateExcelDTO generateExcelDTO) throws EwsException, IOException {
        byte[] excelFileInBytes = excelService.generateExcel(generateExcelDTO);
        if (excelFileInBytes == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, StringUtils.join(HttpHeaders.CONTENT_TYPE, ",", HttpHeaders.CONTENT_DISPOSITION));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "cuadrante.xls");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel");
            return new ResponseEntity<>(excelFileInBytes, headers, HttpStatus.OK);
        }
    }
}
