package tfg.luismiguel.ews.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfg.luismiguel.ews.dto.WeekCreationDTO;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.service.TemporalService;

@RestController
@RequestMapping("/api/temporal")
public class TemporalController {
    @Autowired
    TemporalService temporalService;

    @Operation(summary = "Create a week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the week",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Week.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Week not created",
                    content = @Content)})
    @PostMapping("/create/week")
    public ResponseEntity<Week> createTouristInformer(@RequestBody WeekCreationDTO weekCreationDTO) {
        Week week = temporalService.createWeek(weekCreationDTO);
        return new ResponseEntity<>(week, HttpStatus.CREATED);
    }
}
