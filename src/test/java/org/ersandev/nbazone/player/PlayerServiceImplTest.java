package org.ersandev.nbazone.player;

import org.ersandev.nbazone.dto.PlayerRequestDto;
import org.ersandev.nbazone.dto.PlayerResponseDto;
import org.ersandev.nbazone.exceptions.PlayerNotFoundException;
import org.ersandev.nbazone.exceptions.TeamNotFoundException;
import org.ersandev.nbazone.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceImplTest {

    private PlayerRepository playerRepository;
    private EntityMapper entityMapper;
    private PlayerServiceImpl playerService;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        playerRepository = mock(PlayerRepository.class);
        entityMapper = mock(EntityMapper.class);
        modelMapper = new ModelMapper();
        MockitoAnnotations.openMocks(this);
        playerService = new PlayerServiceImpl(playerRepository, entityMapper, modelMapper);
    }

    @Test
    void testGetPlayersReturnsMappedDtoList() {
        // Arrange
        Player player1 = new Player();
        player1.setId(1L);
        player1.setPlayerName("LeBron James");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setPlayerName("Stephen Curry");

        List<Player> mockPlayers = List.of(player1, player2);

        when(playerRepository.findAll()).thenReturn(mockPlayers);

        PlayerResponseDto dto1 = new PlayerResponseDto();
        dto1.setPlayerName("LeBron James");

        PlayerResponseDto dto2 = new PlayerResponseDto();
        dto2.setPlayerName("Stephen Curry");

        when(entityMapper.entityToDto(player1, PlayerResponseDto.class)).thenReturn(dto1);
        when(entityMapper.entityToDto(player2, PlayerResponseDto.class)).thenReturn(dto2);

        // Act
        List<PlayerResponseDto> result = playerService.getPlayers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("LeBron James", result.get(0).getPlayerName());
        assertEquals("Stephen Curry", result.get(1).getPlayerName());
        verify(playerRepository, times(1)).findAll();
        verify(entityMapper, times(2)).entityToDto(any(), eq(PlayerResponseDto.class));
    }

    @Test
    void testGetPlayersFromTeamReturnsMappedDtoList() {
        // Arrange
        String teamName = "Lakers";

        Player player1 = new Player();
        player1.setId(1L);
        player1.setPlayerName("LeBron James");
        player1.setTeam("Lakers");

        List<Player> mockPlayers = List.of(player1);
        when(playerRepository.findAllByTeamIgnoreCase(teamName)).thenReturn(Optional.of(mockPlayers));

        PlayerResponseDto dto1 = new PlayerResponseDto();
        dto1.setPlayerName("LeBron James");

        when(entityMapper.entityToDto(player1, PlayerResponseDto.class)).thenReturn(dto1);

        // Act
        List<PlayerResponseDto> result = playerService.getPlayersFromTeam(teamName);

        // Assert
        assertEquals(1, result.size());
        assertEquals("LeBron James", result.get(0).getPlayerName());
        verify(playerRepository, times(1)).findAllByTeamIgnoreCase(teamName);
    }

    @Test
    void testGetPlayersFromTeamThrowsExceptionWhenTeamNotFound() {
        // Arrange
        String teamName = "UnknownTeam";
        when(playerRepository.findAllByTeamIgnoreCase(teamName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TeamNotFoundException.class, () -> playerService.getPlayersFromTeam(teamName));
        verify(playerRepository, times(1)).findAllByTeamIgnoreCase(teamName);
    }

    @Test
    void testGetPlayersByNameReturnsMappedDtoList() {
        // given
        String playerName = "LeBron";
        Player player1 = new Player(); player1.setPlayerName("LeBron");
        Player player2 = new Player(); player2.setPlayerName("LeBron");

        List<Player> playerList = List.of(player1, player2);
        List<PlayerResponseDto> dtoList = List.of(
                new PlayerResponseDto(), new PlayerResponseDto()
        );

        when(playerRepository.findAllByPlayerName(playerName)).thenReturn(Optional.of(playerList));
        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        List<PlayerResponseDto> result = playerService.getPlayersByName(playerName);

        // then
        assertEquals(2, result.size());
        verify(playerRepository).findAllByPlayerName(playerName);
        verify(entityMapper, times(2)).entityToDto(any(Player.class), eq(PlayerResponseDto.class));
    }

    @Test
    void testGetPlayersByNameThrowsExceptionWhenNotFound() {
        // given
        String playerName = "Unknown";
        when(playerRepository.findAllByPlayerName(playerName)).thenReturn(Optional.empty());

        // when + then
        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.getPlayersByName(playerName);
        });

        verify(playerRepository).findAllByPlayerName(playerName);
    }

    @Test
    void testGetPlayersByAgeReturnsDtoList() {
        // given
        int age = 30;
        Player player1 = new Player(); player1.setAge(age);
        Player player2 = new Player(); player2.setAge(age);

        List<Player> playerList = List.of(player1, player2);

        when(playerRepository.findAllByAge(age)).thenReturn(Optional.of(playerList));
        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        List<PlayerResponseDto> result = playerService.getPlayersByAge(age);

        // then
        assertEquals(2, result.size());
        verify(playerRepository).findAllByAge(age);
        verify(entityMapper, times(2)).entityToDto(any(Player.class), eq(PlayerResponseDto.class));
    }

    @Test
    void testGetPlayersByAgeThrowsExceptionWhenNoneFound() {
        // given
        int age = 40;
        when(playerRepository.findAllByAge(age)).thenReturn(Optional.empty());

        // when + then
        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.getPlayersByAge(age);
        });

        verify(playerRepository).findAllByAge(age);
    }

    @Test
    void testSortPlayersByAgeAscending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "asc";

        Player player1 = new Player(); player1.setAge(25);
        Player player2 = new Player(); player2.setAge(30);
        Page<Player> playerPage = new PageImpl<>(List.of(player1, player2));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "age"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByAge(page, size, direction);

        // then
        assertEquals(2, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "age")));
    }

    @Test
    void testSortPlayersByAgeDescending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "desc";

        Player player1 = new Player(); player1.setAge(35);
        Player player2 = new Player(); player2.setAge(30);
        Page<Player> playerPage = new PageImpl<>(List.of(player1, player2));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "age"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByAge(page, size, direction);

        // then
        assertEquals(2, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "age")));
    }

    @Test
    void testSortPlayersByAgeInvalidDirectionDefaultsToAscending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "invalid_direction";

        Player player = new Player(); player.setAge(28);
        Page<Player> playerPage = new PageImpl<>(List.of(player));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "age"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByAge(page, size, direction);

        // then
        assertEquals(1, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "age")));
    }

    @Test
    void testSortPlayersByPointAscending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "asc";

        Player player1 = new Player(); player1.setPts(10.2);
        Player player2 = new Player(); player2.setPts(15.6);
        Page<Player> playerPage = new PageImpl<>(List.of(player1, player2));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "pts"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByPoint(page, size, direction);

        // then
        assertEquals(2, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "pts")));
    }

    @Test
    void testSortPlayersByPointDescending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "desc";

        Player player1 = new Player(); player1.setPts(30.1);
        Player player2 = new Player(); player2.setPts(25.3);
        Page<Player> playerPage = new PageImpl<>(List.of(player1, player2));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "pts"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByPoint(page, size, direction);

        // then
        assertEquals(2, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "pts")));
    }

    @Test
    void testSortPlayersByPointInvalidDirectionDefaultsToAscending() {
        // given
        int page = 0;
        int size = 2;
        String direction = "wrong";

        Player player = new Player(); player.setPts(20.5);
        Page<Player> playerPage = new PageImpl<>(List.of(player));

        when(playerRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "pts"))))
                .thenReturn(playerPage);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        Page<PlayerResponseDto> result = playerService.sortPlayersByPoint(page, size, direction);

        // then
        assertEquals(1, result.getContent().size());
        verify(playerRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "pts")));
    }

    @Test
    void testTopTenPlayersForFilter_ShouldReturnTopTen() {
        // given
        String filter = "points";
        Pageable topTen = PageRequest.of(0, 10);

        Player player1 = new Player();
        Player player2 = new Player();

        List<Player> players = List.of(player1, player2);

        when(playerRepository.findTopTenByFilter(filter, topTen))
                .thenReturn(players);

        when(entityMapper.entityToDto(any(Player.class), eq(PlayerResponseDto.class)))
                .thenReturn(new PlayerResponseDto());

        // when
        List<PlayerResponseDto> result = playerService.topTenPlayersForFilter(filter);

        // then
        assertEquals(2, result.size());
        verify(playerRepository).findTopTenByFilter(filter, topTen);
        verify(entityMapper, times(2)).entityToDto(any(Player.class), eq(PlayerResponseDto.class));
    }

    @Test
    void testTopTenPlayersForFilter_ShouldReturnEmptyList_WhenNoPlayersFound() {
        // given
        String filter = "rebounds";
        Pageable topTen = PageRequest.of(0, 10);

        when(playerRepository.findTopTenByFilter(filter, topTen))
                .thenReturn(Collections.emptyList());

        // when
        List<PlayerResponseDto> result = playerService.topTenPlayersForFilter(filter);

        // then
        assertTrue(result.isEmpty());
        verify(playerRepository).findTopTenByFilter(filter, topTen);
    }

    @Test
    void testAddPlayer_ShouldSaveAndReturnDto() {
        // given
        PlayerRequestDto requestDto = new PlayerRequestDto();
        Player playerEntity = new Player();
        Player savedPlayer = new Player();
        PlayerResponseDto responseDto = new PlayerResponseDto();

        when(entityMapper.dtoToEntity(requestDto, Player.class)).thenReturn(playerEntity);
        when(playerRepository.save(playerEntity)).thenReturn(savedPlayer);
        when(entityMapper.entityToDto(savedPlayer, PlayerResponseDto.class)).thenReturn(responseDto);

        // when
        PlayerResponseDto result = playerService.addPlayer(requestDto);

        // then
        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(entityMapper).dtoToEntity(requestDto, Player.class);
        verify(playerRepository).save(playerEntity);
        verify(entityMapper).entityToDto(savedPlayer, PlayerResponseDto.class);
    }

    @Test
    void testUpdatePlayer_ShouldUpdateAndReturnDto() {
        // given
        Long playerId = 1L;
        PlayerRequestDto requestDto = new PlayerRequestDto();
        requestDto.setPlayerName("LeBron");

        Player existingPlayer = new Player();
        existingPlayer.setPlayerName("Old Name");

        Player updatedPlayer = new Player();
        updatedPlayer.setPlayerName("LeBron");

        PlayerResponseDto responseDto = new PlayerResponseDto();
        responseDto.setPlayerName("LeBron");

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(entityMapper.entityToDto(updatedPlayer, PlayerResponseDto.class)).thenReturn(responseDto);

        // when
        PlayerResponseDto result = playerService.updatePlayer(playerId, requestDto);

        // then
        assertNotNull(result);
        assertEquals("LeBron", result.getPlayerName());

        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
        verify(entityMapper).entityToDto(updatedPlayer, PlayerResponseDto.class);
}

    @Test
    void testUpdatePlayer_ShouldThrowException_WhenPlayerNotFound() {
        // given
        Long playerId = 1L;
        PlayerRequestDto requestDto = new PlayerRequestDto();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // then
        assertThrows(PlayerNotFoundException.class, () -> playerService.updatePlayer(playerId, requestDto));

        verify(playerRepository).findById(playerId);
        // verify(modelMapper, never()).map(any(), any());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void testDeletePlayer_ShouldDeleteSuccessfully_WhenPlayerExists() {
        // given
        Long playerId = 1L;
        Player player = new Player();
        player.setId(playerId);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // when
        playerService.deletePlayer(playerId);

        // then
        verify(playerRepository).findById(playerId);
        verify(playerRepository).deleteById(playerId);
    }

    @Test
    void testDeletePlayer_ShouldThrowException_WhenPlayerNotFound() {
        // given
        Long playerId = 1L;
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // then
        assertThrows(PlayerNotFoundException.class, () -> playerService.deletePlayer(playerId));

        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).deleteById(any());
    }


}
