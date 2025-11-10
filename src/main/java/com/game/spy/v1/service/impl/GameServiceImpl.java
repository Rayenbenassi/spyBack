package com.game.spy.v1.service.impl;

import com.game.spy.v1.model.GameSession;
import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Question;
import com.game.spy.v1.model.Round;
import com.game.spy.v1.repo.*;
import com.game.spy.v1.service.GameService;
import com.game.spy.v1.service.PlayerService;
import com.game.spy.v1.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class GameServiceImpl implements GameService {
    private final GameSessionRepo gameSessionRepo;
    private final PlayerRepo playerRepo;
    private final QuestionRepo questionRepo;
    private final RoundRepo roundRepo;
    private final VoteRepo voteRepo;
    private final QuestionService questionService;
    private final PlayerService playerService;

    private final Random random = new Random();


    // create new game session
    @Override
    public GameSession createNewGameSession(List<String> playersNames){
        GameSession session = new GameSession();
        gameSessionRepo.save(session);

        playersNames.forEach(name->{
            Player player = Player.builder()
                    .name(name)
                    .session(session)
                    .build();
            playerRepo.save(player);
            session.getPlayers().add(player);
        });

        return session;
    }

    // start new round
    @Override
    public Round startNewRound(Long sessionId){
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(()-> new IllegalArgumentException("session not found"));
        Question question = questionService.getRandomQuestion();

        // choose random spy
        List<Player> players = session.getPlayers();
        Player spy = players.get(random.nextInt(players.size()));

        Round round = Round.builder()
                .session(session)
                .question(question)
                .spy(spy)
                .roundNumber(session.getCurrentRound()+1)
                .build();

        session.setCurrentRound(round.getRoundNumber());
        roundRepo.save(round);
        session.getRounds().add(round);

        return round;
    }

    //end round and calculate scores


    @Override
    public void finishRound(Long roundId){
        Round round = roundRepo.findById(roundId)
                .orElseThrow(()->new IllegalArgumentException("Round not found"));

        //determine who got the most votes
        List<Object[]> results = voteRepo.countVotesGrouped(roundId);

        if(results.isEmpty()) return;

        Long mostVotedPlayerId = (Long) results.get(0)[0];
        long votesCount = (long) results.get(0)[1];

        Player spy = round.getSpy();

        if(spy.getId().equals(mostVotedPlayerId)){
            //Players guessed correctly → all except liar gain points
            round.getSession().getPlayers().forEach(p->{
                if(!p.getId().equals(spy.getId())) {
                    playerService.updateScore(p.getId(),10);
                };

            });}
        else{

             playerService.updateScore(spy.getId(),20);
        }

        round.setCompleted(true);
        roundRepo.save(round);
    }

    // --- End session ---
    @Override
    public void finishSession(Long sessionId) {
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setFinished(true);
        gameSessionRepo.save(session);
    }

    @Override
    public List<GameSession> getAllSessions() {
        return gameSessionRepo.findAll();
    }

    @Override
    public GameSession getSessionStatus(Long sessionId) {
        return gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    @Override
    public Round nextRound(Long sessionId , Long currentRound) {
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.isFinished()) {
            throw new RuntimeException("Session already finished");
        }
        finishRound(currentRound);
        finishRound(currentRound);
        return startNewRound(sessionId);
    }
}
