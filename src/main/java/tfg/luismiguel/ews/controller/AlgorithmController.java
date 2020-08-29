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
import tfg.luismiguel.ews.dto.FillWeekDTO;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.service.AlgorithmService;

import java.util.List;

@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;

    @Operation(summary = "Fill a week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fill the week",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Week not created",
                    content = @Content)})
    @PutMapping("/fill/week")
    public ResponseEntity<Boolean> fillWeek(@RequestBody FillWeekDTO fillWeekDTO) throws EwsException {
        algorithmService.fillCompleteWeek(fillWeekDTO);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }
}
