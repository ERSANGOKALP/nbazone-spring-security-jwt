package org.ersandev.nbazone.player;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByAge(int age);

    @Query(value = "SELECT p FROM Player p ORDER BY CASE " +
                    "WHEN :filter = 'dreb' THEN p.dreb " +
                    "WHEN :filter = 'reb' THEN p.reb " +
                    "WHEN :filter = 'ast' THEN p.ast " +
                    "WHEN :filter = 'stl' THEN p.stl " +
                    "WHEN :filter = 'blk' THEN p.blk " +
                    "ELSE p.id END desc ")
    List<Player> findTopTenByFilter(String filter, Pageable pageable);

    Optional<List<Player>> findAllByTeamIgnoreCase(String team);

    Optional<List<Player>> findAllByPlayerName(String playerName);

    Optional<List<Player>> findAllByAge(int age);
}
