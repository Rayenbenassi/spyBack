package com.game.spy.v1.dto;


import com.game.spy.v1.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {

    private Long id;
    private String name;
    private int score;
    private boolean isEliminated;

    public static PlayerDto toDto(Player player) {
        return new PlayerDto(
                player.getId(),
                player.getName(),
                player.getScore(),
                player.getIsEliminated()
        );
    }
}
