package org.ersandev.nbazone.player;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ersandev.nbazone.dto.PlayerRequestDto;
import org.ersandev.nbazone.dto.PlayerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/getAllPlayers")
    @Operation(summary = "Get all players", description = "Fetches all players from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players fetched successfully"),
            @ApiResponse(responseCode = "500",description = "Internal server error")
    })
    public ResponseEntity<List<PlayerResponseDto>> getPlayers(){
        return ResponseEntity.ok(playerService.getPlayers());
    }

    @GetMapping("/team/{team}")
    @Operation(summary = "Get players by team",description = "Fetch players by their team name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Players fetched successfully"),
            @ApiResponse(responseCode = "404",description = "Team not found"),
            @ApiResponse(responseCode = "500",description = "Internal server error")
    })
    public ResponseEntity<List<PlayerResponseDto>> getPlayersByTeam(
            @Parameter(description = "Name of the team to fetch players for", example = "Lakers")
            @PathVariable String team){
        return ResponseEntity.ok(playerService.getPlayersFromTeam(team));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get players by name",description = "Fetch players matching the provided name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Players fetched successfully"),
            @ApiResponse(responseCode = "404",description = "Player not found"),
            @ApiResponse(responseCode = "500",description = "Internal server error")
    })
    public ResponseEntity<List<PlayerResponseDto>> getPlayersByName(
            @Parameter(description = "Name of the player to fetch players for", example = "Alperen")
            @PathVariable String name){
        return ResponseEntity.ok(playerService.getPlayersByName(name));
    }

    @GetMapping("/age/{age}")
    @Operation(summary = "Get players by age",description = "Fetch players matching the provided age")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Players fetched successfully"),
            @ApiResponse(responseCode = "404",description = "Players with gieven age not found"),
            @ApiResponse(responseCode = "500",description = "Internal server error")
    })
    public List<PlayerResponseDto> getPlayersByAge(
            @Parameter(description = "Age of the player to fetch players for", example = "24")
            @PathVariable int age){
        return playerService.getPlayersByAge(age);
    }

    @GetMapping("/age/page/{page}/size/{size}/direction/{direction}")
    @Operation(summary = "Sort players by age", description = "Sort players by age with pagination and direction(asc/desc).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Players fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sorting parameters"),
            @ApiResponse(responseCode = "500",description = "Internal server error")
    })
    ResponseEntity<Page<PlayerResponseDto>> sortPlayersByAge(
            @Parameter(description = "3 parameters to sort by age,in order -> Page number,Size of the Page and direction(esc/desc)",example = "/1/10/esc")
            @PathVariable int page,
            @PathVariable int size,
            @PathVariable String direction){
        return ResponseEntity.ok(playerService.sortPlayersByAge(page, size, direction));
    }

    @GetMapping("/point/page/{page}/size/{size}/direction/{direction}")
    @Operation(summary = "Get players by point", description = "Sort players by points and pagination and direction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players sorted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sorting parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<PlayerResponseDto>> sortPlayersByPoint(
            @Parameter(description = "3 parameters to sort by point,in order -> Page number,Size of the Page and direction(esc/desc)",example = "/1/10/esc")
            @PathVariable int page,
            @PathVariable int size,
            @PathVariable String direction){

        return ResponseEntity.ok(playerService.sortPlayersByPoint(page, size, direction));
    }

    @GetMapping("/top10/{filter}")
    @Operation(summary = "Get top ten players by filter", description = "Get top 10 players based on a specific filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top 10 players fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PlayerResponseDto>> topTenPlayersForFilter(
            @Parameter(description = "Add a filter to sort top ten players by given filter",example = "reb")
            @PathVariable String filter){

        return ResponseEntity.ok(playerService.topTenPlayersForFilter(filter));
    }

    @PostMapping
    @Operation(summary = "Create a new player",description = "Create a new player record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Player created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid player data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PlayerResponseDto> createPlayer(
            @Parameter(description = "Give valid player object to add new player")
            @RequestBody @Valid PlayerRequestDto player){
        return ResponseEntity.ok(playerService.addPlayer(player));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    @Operation(summary = "Update an existing player", description = "Update an existing player's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player updated successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "400", description = "Invalid player data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PlayerResponseDto> updatePlayer(
            @Parameter(description = "Give exist player id and valid player object to update existing player")
            @PathVariable Long id,
            @RequestBody @Valid PlayerRequestDto player){

        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a player", description = "Delete a player by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Player deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "Give exist player id to delete player" , example = "/22")
            @PathVariable Long id){
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

}
