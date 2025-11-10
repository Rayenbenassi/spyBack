package com.game.spy.v1.service;

import com.game.spy.v1.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class GameServiceTest {

    @Autowired
    GameServiceImpl gameService;

    @Test
    void shouldCreateSessionAndStartRound() {
        var session = gameService.createNewGameSession(List.of("Aymen", "Olfa", "Rayen"));
        var round = gameService.startNewRound(session.getId());
        assertThat(round.getSpy()).isNotNull();
        assertThat(round.getQuestion()).isNotNull();
    }
}

