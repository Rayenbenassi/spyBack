package com.game.spy.v1.controller;

import com.game.spy.v1.dto.VoteDto;
import com.game.spy.v1.dto.VoteRequest;
import com.game.spy.v1.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public VoteDto castVote(@RequestBody VoteRequest request){
        return VoteDto.toDto(
                voteService.castVote(request.getRoundId(), request.getVoterId(), request.getVotedForId())
        );

    }

    @GetMapping("/round/{roundId}")
    public List<VoteDto> getVotesByRound(@PathVariable Long roundId) {
        return voteService.getVotesByRound(roundId)
                .stream()
                .map(VoteDto::toDto)
                .toList();
    }
}
