package com.game.spy.v1.service;

import com.game.spy.v1.model.Player;

import java.util.List;

public interface PlayerService {
    List<Player> getPlayersBySession(Long sessionId);
    Player updateScore(Long playerId, int delta);
}
