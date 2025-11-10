package com.game.spy.v1.repo;

import com.game.spy.v1.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameSessionRepo extends JpaRepository<GameSession, Long> {

}
