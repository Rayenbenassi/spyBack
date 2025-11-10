package com.game.spy.v1.service.impl;


import com.game.spy.v1.model.Player;
import com.game.spy.v1.repo.PlayerRepo;
import com.game.spy.v1.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl  implements PlayerService {
    private final PlayerRepo playerRepo;

    @Override
    public List<Player> getPlayersBySession(Long sessionId) {
        return playerRepo.findBySessionId(sessionId);
    }

    @Override
    public Player updateScore(Long playerId, int delta) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        player.setScore(player.getScore() + delta);
        return playerRepo.save(player);
    }


}
