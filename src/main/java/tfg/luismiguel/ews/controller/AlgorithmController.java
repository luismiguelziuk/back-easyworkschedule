package tfg.luismiguel.ews.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfg.luismiguel.ews.dto.algorithm.CleanWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.FillWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.WeekKnappSackDTO;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.service.AlgorithmService;
import tfg.luismiguel.ews.service.impl.AlgorithmServiceImpl;

@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;

    @Operation(summary = "Fill a week one time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fill the week",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Week not created",
                    content = @Content)})
    @PutMapping("/fill/week")
    public ResponseEntity<WeekKnappSackDTO> fillWeek(@RequestBody FillWeekDTO fillWeekDTO) throws EwsException {
        AlgorithmServiceImpl.solution = null;
        WeekKnappSackDTO solution = algorithmService.fillWeekRecursive(fillWeekDTO);
        algorithmService.saveAll(solution, fillWeekDTO);
        return new ResponseEntity<>(solution, HttpStatus.CREATED);
    }

    @Operation(summary = "Clean already filled week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clean the week",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Week not cleaned",
                    content = @Content)})
    @PutMapping("/clean/week")
    public ResponseEntity<String> cleanWeek(@RequestBody CleanWeekDTO cleanWeekDTO) throws EwsException {
        algorithmService.cleanWeek(cleanWeekDTO);
        return new ResponseEntity<>("Week have cleaned", HttpStatus.CREATED);
    }
}
