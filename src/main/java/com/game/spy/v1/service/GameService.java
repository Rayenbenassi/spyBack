package com.game.spy.v1.service;

import com.game.spy.v1.dto.SessionConfigDto;
import com.game.spy.v1.model.GameSession;
import com.game.spy.v1.model.Round;

import java.util.List;

public interface GameService {
    GameSession createNewGameSession(List<String> playersNames, SessionConfigDto sessionConfigDto);
     List<GameSession> getAllSessions();
     void finishSession(Long sessionId);
     Round startNewRound(Long sessionId);
     GameSession getSessionStatus(Long sessionId) ;
    public void finishRound(Long roundId);

    Round nextRound(Long sessionId,Long currentRound);
}
