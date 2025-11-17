package com.game.spy.v1.dto;

import com.game.spy.v1.model.GameSession;
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
    private int currentRound;
    private boolean finished;
    private Integer numberOfRounds;
    private Category category;
    private List<SimplePlayerDto> players; // Changed from Player to SimplePlayerDto
    private List<SimpleRoundDto> rounds;   // Changed from Round to SimpleRoundDto

    public static GameSessionDto toDto(GameSession gameSession) {
        // Add debug logging to see what's happening
        System.out.println("🔄 Converting GameSession to DTO - ID: " + gameSession.getId());

        if (gameSession.getPlayers() != null) {
            System.out.println("🔍 Original player scores:");
            gameSession.getPlayers().forEach(player ->
                    System.out.println("   " + player.getName() + ": " + player.getScore())
            );
        }

        GameSessionDto dto = GameSessionDto.builder()
                .id(gameSession.getId())
                .currentRound(gameSession.getCurrentRound())
                .finished(gameSession.isFinished())
                .numberOfRounds(gameSession.getNumberOfRounds())
                .category(gameSession.getCategory())
                .players(gameSession.getPlayers().stream()
                        .map(SimplePlayerDto::fromPlayer)
                        .collect(Collectors.toList()))
                .rounds(gameSession.getRounds().stream()
                        .map(SimpleRoundDto::fromRound)
                        .collect(Collectors.toList()))
                .build();

        // Debug the DTO after conversion
        if (dto.getPlayers() != null) {
            System.out.println("✅ DTO player scores:");
            dto.getPlayers().forEach(player ->
                    System.out.println("   " + player.getName() + ": " + player.getScore())
            );
        }

        return dto;
    }

    // Remove the toEntity method or keep it only if needed for creating sessions
    public static GameSession toEntity(GameSessionDto gameSessionDto) {
        // This might not be needed for your frontend responses
        // Only include if you need to convert DTO back to entity for some reason
        GameSession gameSession = new GameSession();
        gameSession.setId(gameSessionDto.getId());
        gameSession.setCurrentRound(gameSessionDto.getCurrentRound());
        gameSession.setFinished(gameSessionDto.isFinished());
        gameSession.setNumberOfRounds(gameSessionDto.getNumberOfRounds());
        gameSession.setCategory(gameSessionDto.getCategory());
        // Note: We don't set players and rounds here as they are complex entities
        return gameSession;
    }
}