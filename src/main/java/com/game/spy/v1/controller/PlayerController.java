package com.game.spy.v1.controller;

import com.game.spy.v1.dto.EliminationResult;
import com.game.spy.v1.dto.GameSessionDto;
import com.game.spy.v1.dto.PlayerDto;
import com.game.spy.v1.model.GameSession;
import com.game.spy.v1.service.GameService;
import com.game.spy.v1.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;
    private final GameService gameService;


    @PostMapping("/round/{roundId}/eliminate/{playerId}")
    public ResponseEntity<EliminationResult> eliminatePlayer(
            @PathVariable long roundId,
            @PathVariable long playerId
    ) {
        // First eliminate the player
        playerService.eliminatePlayer(playerId, roundId);

        GameSession updatedSession = gameService.finishRound(roundId);

        // Create elimination result response
        EliminationResult result = new EliminationResult();
        result.setEliminatedPlayer(PlayerDto.toDto(playerService.getPlayerById(playerId)));
        result.setSessionStatus(GameSessionDto.toDto(updatedSession));
        result.setGameContinues(!updatedSession.isFinished());
        result.setMessage("Player eliminated successfully");

        return ResponseEntity.ok(result);
    }
}
