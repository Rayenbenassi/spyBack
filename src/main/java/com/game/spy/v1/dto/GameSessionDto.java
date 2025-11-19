package com.game.spy.v1.dto;

import com.game.spy.v1.model.GameMode;
import com.game.spy.v1.model.GameSession;
import com.game.spy.v1.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import com.game.spy.v1.model.Category;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionDto {
    private Long id;
    private GameMode gameMode;
    private int currentRound;
    private boolean finished;
    private Integer numberOfRounds;
    private Category category;
    private List<SimplePlayerDto> players;
    private List<SimpleRoundDto> rounds;
    private String spyAssignments;
    private int spiesCount;

    public static GameSessionDto toDto(GameSession gameSession) {
        System.out.println("🔄 Converting GameSession to DTO - ID: " + gameSession.getId());

        // Enhanced debugging with more details
        if (gameSession.getPlayers() != null) {
            System.out.println("🔍 Original player scores and status:");
            gameSession.getPlayers().forEach(player ->
                    System.out.println("   " + player.getName() +
                            " | Score: " + player.getScore() +
                            " | Eliminated: " + player.getIsEliminated() +
                            " | ID: " + player.getId())
            );

            // Calculate total scores for verification
            int totalScore = gameSession.getPlayers().stream()
                    .mapToInt(Player::getScore)
                    .sum();
            System.out.println("📊 Total session score: " + totalScore);
        }

        // Build the DTO with enhanced error handling
        try {
            GameSessionDto dto = GameSessionDto.builder()
                    .id(gameSession.getId())
                    .currentRound(gameSession.getCurrentRound())
                    .finished(gameSession.isFinished())
                    .numberOfRounds(gameSession.getNumberOfRounds())
                    .category(gameSession.getCategory())
                    .players(convertPlayersToDto(gameSession.getPlayers()))
                    .rounds(convertRoundsToDto(gameSession.getRounds()))
                    .gameMode(gameSession.getGameMode())
                    .spiesCount(gameSession.getSpiesCount())
                    .spyAssignments(gameSession.getSpyAssignments())
                    .build();

            // Verify DTO conversion
            if (dto.getPlayers() != null) {
                System.out.println("✅ DTO player scores after conversion:");
                dto.getPlayers().forEach(player ->
                        System.out.println("   " + player.getName() +
                                ": " + player.getScore() +
                                " | Eliminated: " + player.isEliminated())
                );

                int dtoTotalScore = dto.getPlayers().stream()
                        .mapToInt(SimplePlayerDto::getScore)
                        .sum();
                System.out.println("📊 DTO total score: " + dtoTotalScore);
            }

            System.out.println("🎯 Game Mode: " + dto.getGameMode());
            System.out.println("📋 Current Round: " + dto.getCurrentRound() + "/" + dto.getNumberOfRounds());
            System.out.println("🏁 Finished: " + dto.isFinished());

            return dto;

        } catch (Exception e) {
            System.err.println("❌ Error converting GameSession to DTO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to convert GameSession to DTO", e);
        }
    }

    private static List<SimplePlayerDto> convertPlayersToDto(List<Player> players) {
        if (players == null) {
            System.out.println("⚠️ Players list is null");
            return List.of();
        }

        System.out.println("👥 Converting " + players.size() + " players to DTO");

        List<SimplePlayerDto> playerDtos = players.stream()
                .map(player -> {
                    try {
                        SimplePlayerDto dto = SimplePlayerDto.fromPlayer(player);
                        System.out.println("   ✅ Converted player: " + player.getName() +
                                " | Score: " + player.getScore() + " -> " + dto.getScore() +
                                " | Eliminated: " + player.getIsEliminated() + " -> " + dto.isEliminated());
                        return dto;
                    } catch (Exception e) {
                        System.err.println("❌ Error converting player " + player.getName() + ": " + e.getMessage());
                        // Return a fallback DTO to prevent complete failure
                        return SimplePlayerDto.builder()
                                .id(player.getId())
                                .name(player.getName())
                                .score(player.getScore())
                                .isEliminated(player.getIsEliminated())
                                .build();
                    }
                })
                .collect(Collectors.toList());

        System.out.println("✅ Successfully converted " + playerDtos.size() + " players");
        return playerDtos;
    }

    private static List<SimpleRoundDto> convertRoundsToDto(List<com.game.spy.v1.model.Round> rounds) {
        if (rounds == null) {
            System.out.println("⚠️ Rounds list is null");
            return List.of();
        }

        System.out.println("🔄 Converting " + rounds.size() + " rounds to DTO");

        List<SimpleRoundDto> roundDtos = rounds.stream()
                .map(round -> {
                    try {
                        SimpleRoundDto dto = SimpleRoundDto.fromRound(round);
                        System.out.println("   ✅ Converted round: " + dto.getRoundNumber() +
                                " | Completed: " + dto.isCompleted());
                        return dto;
                    } catch (Exception e) {
                        System.err.println("❌ Error converting round " + round.getId() + ": " + e.getMessage());
                        // Return a fallback DTO
                        return SimpleRoundDto.builder()
                                .id(round.getId())
                                .roundNumber(round.getRoundNumber())
                                .completed(round.isCompleted())
                                .build();
                    }
                })
                .collect(Collectors.toList());

        System.out.println("✅ Successfully converted " + roundDtos.size() + " rounds");
        return roundDtos;
    }

    public static GameSession toEntity(GameSessionDto gameSessionDto) {
        System.out.println("🔄 Converting DTO to GameSession entity - ID: " + gameSessionDto.getId());

        try {
            GameSession gameSession = new GameSession();
            gameSession.setId(gameSessionDto.getId());
            gameSession.setCurrentRound(gameSessionDto.getCurrentRound());
            gameSession.setFinished(gameSessionDto.isFinished());
            gameSession.setNumberOfRounds(gameSessionDto.getNumberOfRounds());
            gameSession.setCategory(gameSessionDto.getCategory());
            gameSession.setGameMode(gameSessionDto.getGameMode());
            gameSession.setSpiesCount(gameSessionDto.getSpiesCount());
            gameSession.setSpyAssignments(gameSessionDto.getSpyAssignments());

            // Note: We don't set players and rounds here as they are complex entities
            // that should be managed through proper service methods

            System.out.println("✅ Successfully converted DTO to entity");
            return gameSession;

        } catch (Exception e) {
            System.err.println("❌ Error converting DTO to GameSession: " + e.getMessage());
            throw new RuntimeException("Failed to convert DTO to GameSession", e);
        }
    }

    // Helper method to get active players count
    public int getActivePlayersCount() {
        if (players == null) return 0;
        return (int) players.stream()
                .filter(player -> !player.isEliminated()) // FIXED: Use isEliminated()
                .count();
    }

    // Helper method to get eliminated players count
    public int getEliminatedPlayersCount() {
        if (players == null) return 0;
        return (int) players.stream()
                .filter(SimplePlayerDto::isEliminated) // FIXED: Use isEliminated()
                .count();
    }

    // Helper method to get player by ID
    public SimplePlayerDto getPlayerById(Long playerId) {
        if (players == null) return null;
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    // Helper method to check if game is in progress
    public boolean isGameInProgress() {
        return !finished && currentRound > 0 && currentRound <= numberOfRounds;
    }

    // Helper method to get game status description
    public String getGameStatus() {
        if (finished) {
            return "COMPLETED";
        } else if (currentRound == 0) {
            return "NOT_STARTED";
        } else if (currentRound > numberOfRounds) {
            return "OVERTIME";
        } else {
            return "IN_PROGRESS - Round " + currentRound + "/" + numberOfRounds;
        }
    }

    // Helper method to get winning players (highest score)
    public List<SimplePlayerDto> getWinningPlayers() {
        if (players == null || players.isEmpty()) return List.of();

        int maxScore = players.stream()
                .mapToInt(SimplePlayerDto::getScore)
                .max()
                .orElse(0);

        return players.stream()
                .filter(player -> player.getScore() == maxScore)
                .collect(Collectors.toList());
    }

    // Helper method to check if it's a tie game
    public boolean isTieGame() {
        List<SimplePlayerDto> winners = getWinningPlayers();
        return winners.size() > 1;
    }

    @Override
    public String toString() {
        return String.format(
                "GameSessionDto{id=%d, gameMode=%s, currentRound=%d, finished=%s, activePlayers=%d, totalPlayers=%d}",
                id, gameMode, currentRound, finished, getActivePlayersCount(),
                players != null ? players.size() : 0
        );
    }
}