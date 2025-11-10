package com.game.spy.v1.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteRequest {

        private long roundId;
    private long voterId;
    private long votedForId;

}
