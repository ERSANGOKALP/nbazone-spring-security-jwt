package org.ersandev.nbazone.player;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ersandev.nbazone.dto.PlayerRequestDto;
import org.ersandev.nbazone.dto.PlayerResponseDto;
import org.ersandev.nbazone.exceptions.PlayerNotFoundException;
import org.ersandev.nbazone.exceptions.TeamNotFoundException;
import org.ersandev.nbazone.mapper.EntityMapper;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final EntityMapper entityMapper;
    private final ModelMapper modelMapper;

    @Override
    public List<PlayerResponseDto> getPlayers() {
       List<Player> players = playerRepository.findAll();
        return players.stream()
                .map( player -> entityMapper.entityToDto(player,PlayerResponseDto.class))
                .toList();
    }


    @Override
    public List<PlayerResponseDto> getPlayersFromTeam(String teamName) {
        List<Player> players = playerRepository.findAllByTeamIgnoreCase(teamName)
                .orElseThrow( ()-> new TeamNotFoundException("Team " + teamName + " not found"));

          return players.stream()
                .map( player -> entityMapper.entityToDto(player,PlayerResponseDto.class))
                .toList();
    }

    // Burada örnek olması için functional programing yapısı ile Java 8 stream() kodladım
    //İstersek Jpa kullanarak direkt findByPlayerName() metoduda yaratabilirdik
    @Override
    public List<PlayerResponseDto> getPlayersByName(String playerName){
        List<Player> players = playerRepository.findAllByPlayerName(playerName)
                .orElseThrow( ()-> new PlayerNotFoundException("Player " + playerName + " not found"));

        return  players.stream()
                .map(player -> entityMapper.entityToDto(player, PlayerResponseDto.class))
                .toList();
    }

    @Override
    public List<PlayerResponseDto> getPlayersByAge(int age){

        List<Player> players = playerRepository.findAllByAge(age)
                .orElseThrow( ()-> new PlayerNotFoundException("There are no " + age + " years old players in NBA"));

               return players.stream()
                       .map(player -> entityMapper.entityToDto(player, PlayerResponseDto.class))
                       .toList();
    }

    @Override
    public Page<PlayerResponseDto> sortPlayersByAge(int page, int size, String direction){

        Sort sort;

        if(direction.equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC,"age");
        }else if(direction.equalsIgnoreCase("desc")){
            sort = Sort.by(Sort.Direction.DESC,"age");
        }else {
            sort = Sort.by(Sort.Direction.ASC,"age");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Player> playerPage = playerRepository.findAll(pageable);
        return playerPage.map( player -> entityMapper.entityToDto(player,PlayerResponseDto.class));
    }

    @Override
    public Page<PlayerResponseDto> sortPlayersByPoint(int page, int size, String direction){

        Sort sort;

        if(direction.equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC,"pts");
        }else if(direction.equalsIgnoreCase("desc")){
            sort = Sort.by(Sort.Direction.DESC,"pts");
        }else {
            sort = Sort.by(Sort.Direction.ASC,"pts");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Player> playerPage = playerRepository.findAll(pageable);
        return playerPage.map(player -> entityMapper.entityToDto(player,PlayerResponseDto.class));
    }

    @Override
    public List<PlayerResponseDto> topTenPlayersForFilter(String filter){
        Pageable topTen = PageRequest.of(0,10);
        return playerRepository.findTopTenByFilter(filter,topTen).stream()
                .map(player -> entityMapper.entityToDto(player, PlayerResponseDto.class))
                .toList();
    }

    @Override
    public PlayerResponseDto addPlayer(PlayerRequestDto playerRequestDto){
        Player player = entityMapper.dtoToEntity(playerRequestDto, Player.class);
        Player savedPlayer = playerRepository.save(player);
        return entityMapper.entityToDto(savedPlayer,PlayerResponseDto.class);
    }

    @Override
    public PlayerResponseDto updatePlayer(Long id, PlayerRequestDto playerRequestDto){
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found!"));

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(playerRequestDto, player);

        Player savedPlayer = playerRepository.save(player);
        return entityMapper.entityToDto(savedPlayer,PlayerResponseDto.class);
    }

    @Transactional
    @Override
    public void deletePlayer(Long id){
        Player player = playerRepository.findById(id)
                        .orElseThrow(()-> new PlayerNotFoundException("Player not found!"));

        playerRepository.deleteById(player.getId());
    }

}
