package com.game.spy.v1.repo;

import com.game.spy.v1.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepo extends JpaRepository<Round, Long> {

    List<Round> findBySessionId(Long sessionId);

}
