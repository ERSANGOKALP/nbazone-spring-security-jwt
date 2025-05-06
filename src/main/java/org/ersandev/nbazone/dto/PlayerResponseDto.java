package org.ersandev.nbazone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDto {

    private Long id;
    private String playerName;
    private String team;
    private int age;
    private Double min;
    private Double pts;
    private Double fgPercent;
    private Double threePPercent;
    private Double ftPercent;
    private Double dreb;
    private Double reb;
    private Double ast;
    private Double stl;
    private Double blk;


}
