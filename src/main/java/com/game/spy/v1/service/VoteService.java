package com.game.spy.v1.service;

import com.game.spy.v1.model.Vote;

import java.util.List;

public interface VoteService {
    Vote castVote(Long roundId, Long voterId, Long votedForId);
    List<Vote> getVotesByRound(Long roundId);
    List<Vote> getVotesForRound(Long roundId);
}
;