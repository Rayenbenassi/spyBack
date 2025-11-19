package com.game.spy.v1.dto;

import com.game.spy.v1.model.Round;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.game.spy.v1.model.Question;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundDto {
    private Long id;
    private int roundNumber;
    private boolean completed;
    private SimplePlayerDto spy;
    private Question question;
    private String spyData;

    public static RoundDto toDto(Round round) {
        return RoundDto.builder()
                .id(round.getId())
                .roundNumber(round.getRoundNumber())
                .completed(round.isCompleted())
                .spy(SimplePlayerDto.fromPlayer(round.getSpy()))
                .question(round.getQuestion())
                .spyData(round.getSpyData()) // ADD THIS LINE
                .build();
    }
}