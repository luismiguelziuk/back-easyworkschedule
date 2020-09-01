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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tfg.luismiguel.ews.dto.cex.creation.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristInformerUpdateDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointCreationDTO;
import tfg.luismiguel.ews.dto.cex.creation.TouristPointUpdateDTO;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.service.CexService;

@RestController
@RequestMapping("/api/cex")
public class CexController {
    @Autowired
    CexService cexService;

    @Operation(summary = "Create a tourist informer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the tourist informer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TouristInformer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tourist informer not created",
                    content = @Content)})
    @PostMapping("/create/tourist-informer")
    public ResponseEntity<TouristInformer> createTouristInformer(@RequestBody TouristInformerCreationDTO touristInformerDTO) {
        TouristInformer touristInformer = cexService.createTouristInformer(touristInformerDTO);
        return new ResponseEntity<>(touristInformer, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a tourist informer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the tourist informer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TouristInformer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tourist informer not updated",
                    content = @Content)})
    @PutMapping("/update/tourist-informer")
    public ResponseEntity<TouristInformer> updateTouristInformer(@RequestParam String name,
                                                                 @RequestBody TouristInformerUpdateDTO touristInformerDTO) {
        TouristInformer touristInformer = cexService.updateTouristInformer(name, touristInformerDTO);
        return new ResponseEntity<>(touristInformer, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a tourist point")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the tourist point",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TouristInformer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tourist point not created",
                    content = @Content)})
    @PostMapping("/create/tourist-point")
    public ResponseEntity<TouristPoint> createTouristInformer(@RequestBody TouristPointCreationDTO touristPointDTO) {
        TouristPoint touristPoint = cexService.createTouristPoint(touristPointDTO);
        return new ResponseEntity<>(touristPoint, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a tourist point")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the tourist point",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TouristInformer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tourist point not updated",
                    content = @Content)})
    @PutMapping("/update/tourist-point")
    public ResponseEntity<TouristPoint> updateTouristPoint(@RequestParam String name,
                                                                 @RequestBody TouristPointUpdateDTO touristPointDTO) {
        TouristPoint touristPoint = cexService.updateTouristPoint(name, touristPointDTO);
        return new ResponseEntity<>(touristPoint, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created a new team",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TouristInformer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Team not created",
                    content = @Content)})
    @PostMapping("/create/team")
    public ResponseEntity<Team> createTeam(@RequestParam String name) {
        Team team = cexService.createTeam(name);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }


}
