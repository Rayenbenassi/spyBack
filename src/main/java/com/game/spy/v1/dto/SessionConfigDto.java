package com.game.spy.v1.dto;

import com.game.spy.v1.model.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionConfigDto {

    private int totalRounds;
    private Long categoryId;
    private Integer spiesCount;
    private GameMode gameMode = GameMode.CLASSIC;
}
