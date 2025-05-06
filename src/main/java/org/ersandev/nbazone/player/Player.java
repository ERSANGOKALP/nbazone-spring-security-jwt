package org.ersandev.nbazone.player;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,name = "player")
    private String playerName;
    private String team;
    private int age;
    private Double min;
    private Double pts;
    @Column(name = "fg_percent")
    private Double fgPercent;
    @Column(name = "three_p_percent")
    private Double threePPercent;
    @Column(name = "ft_percent")
    private Double ftPercent;
    private Double dreb;
    private Double reb;
    private Double ast;
    private Double stl;
    private Double blk;
}
