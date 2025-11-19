package com.game.spy.v1.repo;

import com.game.spy.v1.model.Player;
import com.game.spy.v1.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
    List<Player> findBySessionId(Long sessionId);
    @Query("SELECT p FROM Player p WHERE p.eliminatedInRoundId = :roundId")
    Optional<Player> findByEliminatedInRoundId(Long roundId);

    @Query("SELECT p FROM Player p WHERE p.session.id = :sessionId AND p.isEliminated = false")
    List<Player> findActivePlayersBySessionId(Long sessionId);

    @Modifying
    @Query("UPDATE Player p SET p.score = p.score + :delta WHERE p.id = :playerId")
    void updatePlayerScore(@Param("playerId") Long playerId, @Param("delta") int delta);




}
