package com.game.spy.v1.repo;

import com.game.spy.v1.model.Round;
import com.game.spy.v1.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepo extends JpaRepository<Vote, Long> {

    List<Vote> findByRoundId(Long roundId);

    // Check if a player already voted in this round
    boolean existsByRoundIdAndVoterId(Long roundId, Long voterId);

    // Count votes for a specific player in a round
    long countByRoundIdAndVotedForId(Long roundId, Long votedForId);

    // Custom query to get top-voted player (optional)
    @Query("SELECT v.votedFor.id, COUNT(v) FROM Vote v WHERE v.round.id = :roundId GROUP BY v.votedFor.id ORDER BY COUNT(v) DESC")
    List<Object[]> countVotesGrouped(@Param("roundId") Long roundId);

    List<Vote> findByRound(Round round);

}
