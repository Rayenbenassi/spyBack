package com.game.spy.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePlayerDto {
    private Long id;
    private String name;
    private int score;
    private boolean isEliminated; // ADD THIS FIELD

    public static SimplePlayerDto fromPlayer(com.game.spy.v1.model.Player player) {
        return SimplePlayerDto.builder()
                .id(player.getId())
                .name(player.getName())
                .score(player.getScore())
                .isEliminated(player.getIsEliminated()) // ADD THIS LINE
                .build();
    }
}