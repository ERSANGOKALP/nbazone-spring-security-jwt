package org.ersandev.nbazone.player;

import jakarta.transaction.Transactional;
import org.ersandev.nbazone.dto.PlayerRequestDto;
import org.ersandev.nbazone.dto.PlayerResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlayerService {
    List<PlayerResponseDto> getPlayers();

    List<PlayerResponseDto> getPlayersFromTeam(String teamName);

    List<PlayerResponseDto> getPlayersByName(String playerName);

    List<PlayerResponseDto> getPlayersByAge(int age);

    Page<PlayerResponseDto> sortPlayersByAge(int page, int size, String direction);

    Page<PlayerResponseDto> sortPlayersByPoint(int page, int size, String direction);

    List<PlayerResponseDto> topTenPlayersForFilter(String filter);

    PlayerResponseDto addPlayer(PlayerRequestDto playerRequestDto);

    PlayerResponseDto updatePlayer(Long id, PlayerRequestDto playerRequestDto);

    @Transactional
    void deletePlayer(Long id);
}
