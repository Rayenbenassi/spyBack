package com.game.spy.v1.dto;

import com.game.spy.v1.model.Category;
import com.game.spy.v1.model.GameSession;
import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Round;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GameSessionDto {

    private Long id;
    private int currentRound = 0;

    private boolean finished = false;

    private Integer numberOfRounds;

    private Category category;

    private List<Player> players = new ArrayList<Player>();

    private List<Round> rounds = new ArrayList<Round>();

    public static GameSessionDto toDto(GameSession gameSession){
        GameSessionDto gameSessionDto = new GameSessionDto();
        gameSessionDto.setId(gameSession.getId());
        gameSessionDto.setFinished(gameSession.isFinished());
        gameSessionDto.setPlayers(gameSession.getPlayers());
        gameSessionDto.setRounds(gameSession.getRounds());
        gameSessionDto.setCurrentRound(gameSession.getCurrentRound());
        gameSessionDto.setCategory((gameSession.getCategory()));
        gameSessionDto.setNumberOfRounds(gameSession.getNumberOfRounds());


        return gameSessionDto;

    }
    public static GameSession toEntity(GameSessionDto gameSessionDto){
        GameSession gameSession =new GameSession();
        gameSession.setId(gameSessionDto.getId());
        gameSession.setCurrentRound(gameSessionDto.getCurrentRound());
        gameSession.setFinished(gameSessionDto.isFinished());
        gameSession.setPlayers(gameSessionDto.getPlayers());
        gameSession.setRounds(gameSessionDto.getRounds());
        gameSession.setCategory((gameSessionDto.getCategory()));
        gameSession.setNumberOfRounds(gameSessionDto.getNumberOfRounds());


        return gameSession;

    }
}
