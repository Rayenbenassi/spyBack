package com.game.spy.v1.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminationResult {
    private PlayerDto eliminatedPlayer;
    private GameSessionDto sessionStatus;
    private boolean gameContinues;
    private String message;

}
