package com.game.spy.v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.fasterxml.jackson.databind.DeserializationFeature;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    private final EntityManager entityManager;

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GameSession createNewGameSession(List<String> playersNames, SessionConfigDto config){
        GameSession session = new GameSession();
        session.setGameMode(config.getGameMode());
        session.setNumberOfRounds(config.getTotalRounds());

        session.setSpyAssignments(null);

        // Set spiesCount - use configured value or calculate based on game mode
        if (config.getGameMode() == GameMode.MULTI_SPY && config.getSpiesCount() != null) {
            session.setSpiesCount(config.getSpiesCount());
            System.out.println("🎯 Using frontend-provided spies count: " + config.getSpiesCount());
        } else {
            // For multi-spy mode without specified count, calculate optimal
            int calculatedSpies = calculateOptimalSpyCount(playersNames.size());
            session.setSpiesCount(calculatedSpies);
            System.out.println("🎯 Using calculated spies count: " + calculatedSpies);
        }

        Category category = categoryRepo.findById(config.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        session.setCategory(category);
        gameSessionRepo.save(session);

        System.out.println("🎮 Creating new game session with " + playersNames.size() + " players");
        System.out.println("🎮 Game Mode: " + config.getGameMode());
        System.out.println("🕵️ Spies Count: " + session.getSpiesCount());
        System.out.println("📋 Rounds: " + config.getTotalRounds() + " | Category: " + category.getName());

        playersNames.forEach(name->{
            Player player = Player.builder()
                    .name(name)
                    .session(session)
                    .isEliminated(false)
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
        List<Player> activePlayers = playerService.getActivePlayersBySession(sessionId);
        if(activePlayers.size() < 3) {
            throw new RuntimeException("Not enough active players to continue");
        }

        Round round;
        if(session.getGameMode().equals(GameMode.CLASSIC)) {
            round = startNewRoundClassicMode(activePlayers, session, question);
        } else {
            round = startNewRoundMultiSpyMode(activePlayers, session, question);
        }

        session.setCurrentRound(round.getRoundNumber());
        roundRepo.save(round);
        session.getRounds().add(round);

        return round;
    }

    public Round startNewRoundClassicMode(List<Player> players,GameSession session,Question question){
        Player spy = players.get(random.nextInt(players.size()));

        SpyData spyData = new SpyData();
        spyData.setSpyId(spy.getId());

        return Round.builder()
                .session(session)
                .question(question)
                .spy(spy)
                .spyData(convertSpyDataToJson(spyData))
                .roundNumber(session.getCurrentRound()+1)
                .build();
    }

    public Round startNewRoundMultiSpyMode(List<Player> players, GameSession session, Question question) {
        System.out.println("🔍 DEBUG - Starting multi-spy round creation");
        System.out.println("🔍 DEBUG - Session spiesCount: " + session.getSpiesCount());
        System.out.println("🔍 DEBUG - Players count: " + players.size());

        List<Player> spies;

        // Check if we already have spy assignments for this session
        if (session.getSpyAssignments() != null && !session.getSpyAssignments().trim().isEmpty()) {
            System.out.println("🎯 Reusing existing spy assignments from session");
            spies = getSpiesFromSessionAssignments(session, players);
        } else {
            System.out.println("🎯 Creating new spy assignments for session");
            // Use the configured spiesCount if provided and valid, otherwise calculate optimal count
            int numberOfSpies;
            if (session.getSpiesCount() > 0 && session.getSpiesCount() < players.size()) {
                numberOfSpies = session.getSpiesCount();
                System.out.println("🎯 Using configured spies count: " + numberOfSpies);
            } else {
                numberOfSpies = calculateOptimalSpyCount(players.size());
                System.out.println("🎯 Using calculated optimal spies count: " + numberOfSpies);
            }

            System.out.println("🔍 Multi-Spy Mode - Selecting " + numberOfSpies + " spies from " + players.size() + " players");
            spies = selectMultipleSpies(players, numberOfSpies);

            // Save the spy assignments to the session
            saveSpyAssignmentsToSession(session, spies);
        }

        System.out.println("🕵️ Session spies: " +
                spies.stream().map(Player::getName).collect(Collectors.toList()));

        SpyData spyData = new SpyData();
        spyData.setSpyIds(spies.stream().map(Player::getId).collect(Collectors.toList()));
        spyData.setMultiSpyMode(true);

        String spyDataJson = convertSpyDataToJson(spyData);
        System.out.println("📄 SpyData JSON: " + spyDataJson);

        Round round = Round.builder()
                .session(session)
                .question(question)
                .spy(spies.get(0)) // Keep first spy for backward compatibility
                .spyData(spyDataJson)
                .roundNumber(session.getCurrentRound() + 1)
                .build();

        System.out.println("✅ Round created with spyData: " + round.getSpyData());

        return round;
    }

    private List<Player> getSpiesFromSessionAssignments(GameSession session, List<Player> activePlayers) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SpyData spyData = objectMapper.readValue(session.getSpyAssignments(), SpyData.class);
            List<Long> spyIds = spyData.getAllSpyIds();

            // Filter to only include active (non-eliminated) spies
            return activePlayers.stream()
                    .filter(player -> spyIds.contains(player.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("❌ Error reading session spy assignments: " + e.getMessage());
            // Fallback: create new assignments
            return createNewSpyAssignments(session, activePlayers);
        }
    }

    private void saveSpyAssignmentsToSession(GameSession session, List<Player> spies) {
        try {
            SpyData spyData = new SpyData();
            spyData.setSpyIds(spies.stream().map(Player::getId).collect(Collectors.toList()));
            spyData.setMultiSpyMode(true);

            String assignmentsJson = objectMapper.writeValueAsString(spyData);
            session.setSpyAssignments(assignmentsJson);
            gameSessionRepo.save(session);

            System.out.println("💾 Saved spy assignments to session: " + assignmentsJson);
        } catch (Exception e) {
            System.out.println("❌ Error saving spy assignments: " + e.getMessage());
        }
    }

    private List<Player> createNewSpyAssignments(GameSession session, List<Player> players) {
        int numberOfSpies = session.getSpiesCount() > 0 ? session.getSpiesCount() : calculateOptimalSpyCount(players.size());
        List<Player> spies = selectMultipleSpies(players, numberOfSpies);
        saveSpyAssignmentsToSession(session, spies);
        return spies;
    }

    private List<Player> selectMultipleSpies(List<Player> players, int numberOfSpies) {
        Collections.shuffle(players);
        return players.stream()
                .limit(Math.min(numberOfSpies, players.size() - 1))
                .collect(Collectors.toList());
    }

    public int calculateOptimalSpyCount(int totalPlayers) {
        if (totalPlayers <= 4) return 1;
        if (totalPlayers <= 6) return 2;
        if (totalPlayers <= 8) return 3;
        return 4;
    }

    private String convertSpyDataToJson(SpyData spyData) {
        try {
            return objectMapper.writeValueAsString(spyData);
        } catch (Exception e) {
            throw new RuntimeException("Error converting spy data to JSON", e);
        }
    }

    public boolean isPlayerSpy(Round round, Player player) {
        try {
            SpyData spyData = objectMapper.readValue(round.getSpyData(), SpyData.class);
            boolean isSpy = spyData.getAllSpyIds().contains(player.getId());
            System.out.println("🔍 Checking if " + player.getName() + " is spy: " + isSpy);
            return isSpy;
        } catch (Exception e) {
            System.out.println("❌ Error checking spy status for " + player.getName() + ": " + e.getMessage());
            return round.getSpy() != null && round.getSpy().getId().equals(player.getId());
        }
    }

    public List<Player> getSpies(Round round) {
        if (round.getSpyData() == null || round.getSpyData().trim().isEmpty()) {
            // Fallback to single spy mode
            return round.getSpy() != null ? List.of(round.getSpy()) : List.of();
        }

        try {
            // Configure ObjectMapper to ignore unknown properties
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            SpyData spyData = objectMapper.readValue(round.getSpyData(), SpyData.class);
            List<Long> spyIds = spyData.getAllSpyIds();

            if (spyIds == null || spyIds.isEmpty()) {
                // Fallback to single spy mode
                return round.getSpy() != null ? List.of(round.getSpy()) : List.of();
            }

            List<Player> spies = playerRepo.findAllById(spyIds);
            System.out.println("🔍 Retrieved " + spies.size() + " spies from database for round " + round.getId());

            // Filter out eliminated spies for active count
            List<Player> activeSpies = spies.stream()
                    .filter(spy -> !spy.getIsEliminated())
                    .collect(Collectors.toList());

            System.out.println("🔍 Active spies: " + activeSpies.size() + " | Total spies: " + spies.size());
            return spies;
        } catch (Exception e) {
            System.out.println("❌ Error parsing spy data: " + e.getMessage());
            // Fallback to single spy mode
            return round.getSpy() != null ? List.of(round.getSpy()) : List.of();
        }
    }

    @Override
    public GameSession finishRound(Long roundId){
        Round round = roundRepo.findById(roundId)
                .orElseThrow(()->new IllegalArgumentException("Round not found"));
        GameSession session = round.getSession();

        if(round.getSession().getGameMode().equals(GameMode.CLASSIC)){
            finishClassicRound(round, roundId);
            // Refresh and return the session for classic mode
            refreshSession(session.getId());
            return gameSessionRepo.findById(session.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found after finishing round"));
        } else {
            // For multi-spy mode, finish the round and check game state
            session = finishMultiSpiesRound(round, roundId);
            return checkMultiSpyGameState(session);
        }
    }

    private void finishClassicRound(Round round, Long roundId){
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

    private GameSession finishMultiSpiesRound(Round round, Long roundId){
        Player eliminatedPlayer = playerService.getEliminatedPlayerByRound(roundId);
        GameSession session = round.getSession();

        System.out.println("🎯 Finishing round: " + roundId);
        System.out.println("📊 Eliminated Player: " + eliminatedPlayer.getName());

        // Get all players for the session to ensure we update everyone
        List<Player> allPlayers = playerService.getPlayersBySession(session.getId());

        if(isPlayerSpy(round, eliminatedPlayer)){
            System.out.println("✅ A spy was caught! Giving 10 points to all agents");

            // Update all non-spy players
            allPlayers.forEach(player -> {
                if(!isPlayerSpy(round, player) && !player.getIsEliminated()) {
                    System.out.println("➕ Giving 10 points to agent: " + player.getName());
                    playerService.updateScore(player.getId(), 10);
                }
            });
        } else {
            System.out.println("🎭 You lost an agent: " + eliminatedPlayer.getName() + " - Giving 20 points to all spies");

            // Update all spy players
            allPlayers.forEach(player -> {
                if(isPlayerSpy(round, player) && !player.getIsEliminated()) {
                    System.out.println("➕ Giving 20 points to spy: " + player.getName());
                    playerService.updateScore(player.getId(), 20);
                }
            });
        }

        round.setCompleted(true);
        roundRepo.save(round);

        // Force refresh and return the updated session
        return refreshAndGetSession(session.getId());
    }

    // Add this helper method
    private GameSession refreshAndGetSession(Long sessionId) {
        entityManager.flush();
        entityManager.clear();

        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Force loading of players with updated scores
        session.getPlayers().forEach(player -> {
            entityManager.refresh(player);
        });

        System.out.println("🔄 Session refreshed - Final scores:");
        session.getPlayers().forEach(player ->
                System.out.println("   " + player.getName() + ": " + player.getScore())
        );

        return session;
    }

    private GameSession checkMultiSpyGameState(GameSession session) {
        refreshSession(session.getId());

        List<Player> activePlayers = playerService.getActivePlayersBySession(session.getId());

        // Get spies from SESSION assignments, not from current round
        List<Player> spies = getSpiesFromSessionAssignments(session, activePlayers);

        // Count ACTIVE spies (not eliminated)
        long activeSpies = spies.stream()
                .filter(spy -> !spy.getIsEliminated())
                .count();

        long activeAgents = activePlayers.size() - activeSpies;

        System.out.println("📊 Game State - Spies: " + activeSpies + " | Agents: " + activeAgents);
        System.out.println("🔍 Total active players: " + activePlayers.size());
        System.out.println("🕵️ Original session spies: " +
                spies.stream().map(Player::getName).collect(Collectors.toList()));
        System.out.println("🔍 Active spies: " + activeSpies);

        if(activeSpies > activeAgents) {
            System.out.println("🎯 SPIES WIN! They outnumber the agents");
            finishMultiSpySession(session, true);
        } else if(activeSpies == 0) {
            System.out.println("🎯 AGENTS WIN! All spies eliminated");
            finishMultiSpySession(session, false);
        } else if(session.getCurrentRound() >= session.getNumberOfRounds()) {
            System.out.println("🎯 AGENTS WIN! Survived all rounds");
            finishMultiSpySession(session, false);
        } else {
            System.out.println("🔄 Game continues to next round - " + activeSpies + " spy/spies still active");
        }

        return gameSessionRepo.findById(session.getId())
                .orElseThrow(() -> new RuntimeException("Session not found after game state check"));
    }

    // Add this helper method to get the current round
    private Round getCurrentRound(GameSession session) {
        return session.getRounds().stream()
                .filter(round -> round.getRoundNumber() == session.getCurrentRound())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Current round not found"));
    }

    private void finishMultiSpySession(GameSession session, boolean spiesWon) {
        System.out.println("🏁 FINAL SCORING - Multi-Spy Session");

        List<Player> spies = getSpies(session.getRounds().get(0));

        if(spiesWon) {
            System.out.println("💰 Spies win! Adding bonus points...");
            spies.forEach(spy -> {
                if(!spy.getIsEliminated()) {
                    System.out.println("➕ Giving 50 bonus points to spy: " + spy.getName());
                    playerService.updateScore(spy.getId(), 50);
                }
            });
        } else {
            System.out.println("💰 Agents win! Adding bonus points...");
            session.getPlayers().forEach(player -> {
                if(!isPlayerSpyInSession(session, player) && !player.getIsEliminated()) {
                    System.out.println("➕ Giving 30 bonus points to agent: " + player.getName());
                    playerService.updateScore(player.getId(), 30);
                }
            });
        }

        session.setFinished(true);
        gameSessionRepo.save(session);
        System.out.println("🎊 Multi-spy session completed!");
    }

    private boolean isPlayerSpyInSession(GameSession session, Player player) {
        Round firstRound = session.getRounds().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No rounds found in session"));

        List<Player> spies = getSpies(firstRound);
        return spies.stream().anyMatch(spy -> spy.getId().equals(player.getId()));
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

        refreshSession(sessionId);

        GameSession freshSession = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found after refresh"));

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

    private void refreshSession(Long sessionId) {
        entityManager.clear();

        GameSession session = gameSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.getPlayers().forEach(player -> {
            entityManager.refresh(player);
        });

        System.out.println("🔄 Session refreshed - Latest scores:");
        session.getPlayers().forEach(player ->
                System.out.println("   " + player.getName() + ": " + player.getScore())
        );
    }
}