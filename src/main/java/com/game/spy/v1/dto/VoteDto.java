package com.game.spy.v1.dto;

import com.game.spy.v1.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private Long id;
    private SimpleRoundDto round;
    private SimplePlayerDto voter;
    private SimplePlayerDto votedFor;

    public static VoteDto toDto(Vote vote) {
        return VoteDto.builder()
                .id(vote.getId())
                .round(SimpleRoundDto.fromRound(vote.getRound()))
                .voter(SimplePlayerDto.fromPlayer(vote.getVoter()))
                .votedFor(SimplePlayerDto.fromPlayer(vote.getVotedFor()))
                .build();
    }
}
