package com.game.spy.v1.service.impl;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.repo.PlayerRepo;
import com.game.spy.v1.service.PlayerService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepo playerRepo;
    private final EntityManager entityManager;

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

        // Use a direct update query to ensure the score is updated
        playerRepo.updatePlayerScore(playerId, delta);

        // Clear the entity manager cache and reload the player
        entityManager.flush();
        entityManager.clear();

        Player updatedPlayer = playerRepo.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found after update"));

        System.out.println("✅ Updated score for player: " + updatedPlayer.getName() +
                " | New Score: " + updatedPlayer.getScore());

        return updatedPlayer;
    }

    @Override
    public Boolean eliminatePlayer(Long playerId, Long roundId) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        player.setIsEliminated(true);
        player.setEliminatedInRoundId(roundId);

        playerRepo.save(player);

        // Clear cache to ensure changes are visible
        entityManager.flush();
        entityManager.clear();

        return true;
    }

    @Override
    public Player getEliminatedPlayerByRound(Long roundId) {
        return playerRepo.findByEliminatedInRoundId(roundId)
                .orElseThrow(() -> new RuntimeException("No eliminated player found for round: " + roundId));
    }

    @Override
    public List<Player> getActivePlayersBySession(Long sessionId) {
        return playerRepo.findActivePlayersBySessionId(sessionId);
    }

    @Override
    public Player getPlayerById(Long playerId){
        return playerRepo.findById(playerId)
                .orElseThrow(() -> new RuntimeException("No player found for id: " + playerId));
    }
}