package com.game.spy.v1.controller;

import com.game.spy.v1.dto.CreateSessionRequest;
import com.game.spy.v1.dto.GameSessionDto;
import com.game.spy.v1.dto.RoundDto;
import com.game.spy.v1.dto.SessionConfigDto;
import com.game.spy.v1.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    // Create a new game session
    @PostMapping("/session")
    public GameSessionDto createNewSession(@RequestBody CreateSessionRequest request) {
        return GameSessionDto.toDto(
                gameService.createNewGameSession(request.getPlayerNames(), request.getSessionConfigDto())
        );
    }

    // Start the first round
    @PostMapping("/{sessionId}/round")
    public RoundDto startNewRound(@PathVariable long sessionId) {
        return RoundDto.toDto(
                gameService.startNewRound(sessionId)
        );
    }

    // Finish a round manually
    @PostMapping("/round/{roundId}/finish")
    public void finishRound(@PathVariable long roundId) {
        gameService.finishRound(roundId);
    }

    // Get current session status (scores, rounds, etc.)
    @GetMapping("/{sessionId}/status")
    public GameSessionDto getSessionStatus(@PathVariable long sessionId) {
        return GameSessionDto.toDto(
                gameService.getSessionStatus(sessionId)
        );
    }

    // Progress to the next round
    @PostMapping("/{sessionId}/next-round/{currentRound}")
    public RoundDto nextRound(
            @PathVariable long sessionId,
            @PathVariable long currentRound
    ) {
        return RoundDto.toDto(
                gameService.nextRound(sessionId, currentRound)
        );
    }

    // End the entire session
    @PostMapping("/{sessionId}/end")
    public void endSession(@PathVariable long sessionId) {
        gameService.finishSession(sessionId);
    }
}
