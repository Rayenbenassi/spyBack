package com.game.spy.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRoundDto {
    private Long id;
    private int roundNumber;
    private boolean completed;

    public static SimpleRoundDto fromRound(com.game.spy.v1.model.Round round) {
        return SimpleRoundDto.builder()
                .id(round.getId())
                .roundNumber(round.getRoundNumber())
                .completed(round.isCompleted())
                .build();
    }
}