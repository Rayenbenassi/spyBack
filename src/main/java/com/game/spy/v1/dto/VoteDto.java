package com.game.spy.v1.dto;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Round;
import com.game.spy.v1.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VoteDto {

    private Long id;
    private Round round;
    private Player voter;
    private Player votedFor;

    public static VoteDto toDto(Vote vote){

        return VoteDto.builder()
                .id(vote.getId())
                .round(vote.getRound())
                .votedFor(vote.getVotedFor())
                .voter(vote.getVoter())
                .build();
    }


}
