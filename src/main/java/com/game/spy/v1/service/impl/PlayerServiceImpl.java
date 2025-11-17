package com.game.spy.v1.service.impl;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.repo.PlayerRepo;
import com.game.spy.v1.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepo playerRepo;

    @Override
    public List<Player> getPlayersBySession(Long sessionId) {
        return playerRepo.findBySessionId(sessionId);
    }

    @Override
    public Player updateScore(Long playerId, int delta) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        System.out.println("🔄 Updating score for player: " + player.getName() +
                " | Current: " + player.getScore() + " | Delta: " + delta);

        player.setScore(player.getScore() + delta);
        Player savedPlayer = playerRepo.save(player);

        System.out.println("✅ Updated score for player: " + savedPlayer.getName() +
                " | New Score: " + savedPlayer.getScore());

        return savedPlayer;
    }
}