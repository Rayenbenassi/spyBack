package com.game.spy.v1.controller;

import com.game.spy.v1.dto.CreateSessionRequest;
import com.game.spy.v1.dto.GameSessionDto;
import com.game.spy.v1.dto.PlayerDto;
import com.game.spy.v1.dto.RoundDto;
import com.game.spy.v1.model.Player;
import com.game.spy.v1.service.GameService;
import com.game.spy.v1.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;

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


    @GetMapping("/{sessionId}/active-players")
    public List<PlayerDto> getActivePlayers(@PathVariable long sessionId) {
        return playerService.getActivePlayersBySession(sessionId)
                .stream()
                .map(PlayerDto::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/round/{roundId}/eliminated-player")
    public PlayerDto getEliminatedPlayer(@PathVariable long roundId) {
        return PlayerDto.toDto(
                playerService.getEliminatedPlayerByRound(roundId)
        );
    }



}
