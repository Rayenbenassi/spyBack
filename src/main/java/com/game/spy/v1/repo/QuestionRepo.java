package com.game.spy.v1.repo;

import com.game.spy.v1.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepo extends JpaRepository <Question, Long> {
    // Get a random question (native query works for H2, Postgres, MySQL)
    @Query(value = "SELECT * FROM question ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question getRandomQuestion();

    // Optional: filter by locale or category
    @Query(value = "SELECT * FROM question WHERE locale = :locale ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question getRandomQuestionByLocale(String locale);
}
