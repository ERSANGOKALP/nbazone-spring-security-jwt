package org.ersandev.nbazone.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
public class PlayerRequestDto {

    @NotBlank
    private String playerName;
    @NotBlank
    private String team;
    @Positive
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
