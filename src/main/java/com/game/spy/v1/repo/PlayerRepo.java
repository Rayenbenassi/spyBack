package com.game.spy.v1.repo;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
    List<Player> findBySessionId(Long sessionId);


}
