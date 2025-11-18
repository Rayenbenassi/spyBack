package com.game.spy.v1.service.impl;

import com.game.spy.v1.dto.SessionConfigDto;
import com.game.spy.v1.model.*;
import com.game.spy.v1.repo.*;
import com.game.spy.v1.service.GameService;
import com.game.spy.v1.service.PlayerService;
import com.game.spy.v1.service.QuestionService;
import jakarta.persistence.EntityManager;
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
    private final RoundRepo roundRepo;
    private final VoteRepo voteRepo;
    private final CategoryRepo categoryRepo;
    private final QuestionService questionService;
    private final PlayerService playerService;
    private final EntityManager entityManager; // Add this

    private final Random random = new Random();

    @Override
    public GameSession createNewGameSession(List<String> playersNames, SessionConfigDto config){
        GameSession session = new GameSession();
        session.setNumberOfRounds(config.getTotalRounds());
        Category category = categoryRepo.findById(config.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        session.setCategory(category);
        gameSessionRepo.save(session);

        System.out.println("🎮 Creating new game session with " + playersNames.size() + " players");
        System.out.println("📋 Rounds: " + config.getTotalRounds() + " | Category: " + category.getName());

        playersNames.forEach(name->{
            Player player = Player.builder()
                    .name(name)
                    .session(session)
                    .score(0)
                    .build();
            playerRepo.save(player);
            session.getPlayers().add(player);
            System.out.println("👤 Created player: " + name + " with ID: " + player.getId());
        });

        return session;
    }

    @Override
    public Round startNewRound(Long sessionId){
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(()-> new IllegalArgumentException("session not found"));
        Long categoryId = session.getCategory().getId();
        Question question = questionService.getRandomQuestionByCategory(categoryId);

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

    @Override
    public void finishRound(Long roundId){
        Round round = roundRepo.findById(roundId)
                .orElseThrow(()->new IllegalArgumentException("Round not found"));

        List<Object[]> results = voteRepo.countVotesGrouped(roundId);

        System.out.println("🎯 Finishing round: " + roundId);
        System.out.println("📊 Vote results: " + results);

        if(results.isEmpty()) {
            System.out.println("❌ No votes found for round: " + roundId);
            round.setCompleted(true);
            roundRepo.save(round);
            return;
        }

        Long mostVotedPlayerId = (Long) results.get(0)[0];
        long votesCount = (long) results.get(0)[1];

        Player spy = round.getSpy();

        System.out.println("🕵️ Spy: " + spy.getName() + " (ID: " + spy.getId() + ")");
        System.out.println("🗳️ Most voted player ID: " + mostVotedPlayerId);
        System.out.println("📈 Votes count: " + votesCount);

        if(spy.getId().equals(mostVotedPlayerId)){
            System.out.println("✅ Spy was caught! Giving points to all non-spy players");
            round.getSession().getPlayers().forEach(p->{
                if(!p.getId().equals(spy.getId())) {
                    System.out.println("➕ Giving 10 points to: " + p.getName());
                    playerService.updateScore(p.getId(), 10);
                }
            });
        } else {
            System.out.println("🎭 Spy escaped! Giving 20 points to spy: " + spy.getName());
            playerService.updateScore(spy.getId(), 20);
        }

        round.setCompleted(true);
        roundRepo.save(round);

        // Force refresh of the session to get updated scores
        refreshSession(round.getSession().getId());

        System.out.println("🏁 Round " + roundId + " completed successfully");
    }

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
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Force refresh to get latest scores
        refreshSession(sessionId);

        // Get fresh session data
        GameSession freshSession = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found after refresh"));

        // Debug: Print current scores
        System.out.println("📊 Session Status - Current Scores:");
        freshSession.getPlayers().forEach(player ->
                System.out.println("   " + player.getName() + ": " + player.getScore())
        );

        return freshSession;
    }

    @Override
    public Round nextRound(Long sessionId , Long currentRound) {
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.isFinished()) {
            throw new RuntimeException("Session already finished");
        }
        finishRound(currentRound);
        return startNewRound(sessionId);
    }

    // Add this method to force refresh the session
    private void refreshSession(Long sessionId) {
        // Clear the persistence context to force fresh data from database
        entityManager.clear();

        // Alternatively, you can refresh each player individually
        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Refresh each player to get updated scores
        session.getPlayers().forEach(player -> {
            entityManager.refresh(player);
        });

        System.out.println("🔄 Session refreshed - Latest scores:");
        session.getPlayers().forEach(player ->
                System.out.println("   " + player.getName() + ": " + player.getScore())
        );
    }
}