package com.game.spy.v1.service.impl;


import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Round;
import com.game.spy.v1.model.Vote;
import com.game.spy.v1.repo.*;
import com.game.spy.v1.service.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteServiceImpl implements VoteService {
    private final VoteRepo voteRepo;
    private final RoundRepo roundRepo;
    private final PlayerRepo playerRepo;

    @Override
    public Vote castVote(Long roundId, Long voterId, Long votedForId) {
        if (voteRepo.existsByRoundIdAndVoterId(roundId, voterId)) {
            throw new IllegalStateException("Player already voted in this round!");
        }

        Round round = roundRepo.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Round not found"));

        Player voter = playerRepo.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found"));

        Player votedFor = playerRepo.findById(votedForId)
                .orElseThrow(() -> new IllegalArgumentException("Voted-for player not found"));

        Vote vote = Vote.builder()
                .round(round)
                .voter(voter)
                .votedFor(votedFor)
                .build();

        return voteRepo.save(vote);
    }
    @Override
    public List<Vote> getVotesByRound(Long roundId) {
        Round round = roundRepo.findById(roundId)
                .orElseThrow(() -> new RuntimeException("Round not found"));
        return voteRepo.findByRound(round);
    }


    @Override
    public List<Vote> getVotesForRound(Long roundId) {
        System.out.println("📋 Getting votes for round: " + roundId);
        List<Vote> votes = voteRepo.findByRoundId(roundId);
        System.out.println("✅ Found " + votes.size() + " votes for round: " + roundId);
        return votes;
    }}
