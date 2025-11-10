package com.game.spy.v1.dto;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Question;
import com.game.spy.v1.model.Round;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundDto {

    private Long id;
    private int roundNumber;
    private boolean completed;
    private Player spy;
    private Question question;

    public static RoundDto toDto(Round round) {
        return RoundDto.builder()
                .id(round.getId())
                .roundNumber(round.getRoundNumber())
                .completed(round.isCompleted())
                .spy(round.getSpy())
                .question(round.getQuestion())
                .build();
    }
}
