package com.game.spy.v1.repo;

import com.game.spy.v1.model.Category;
import com.game.spy.v1.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepo extends JpaRepository <Question, Long> {
    // RANDOM QUESTION
    @Query(value = "SELECT * FROM question ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question getRandomQuestion();

    // RANDOM QUESTION BY CATEGORY
    @Query(value = "SELECT * FROM question WHERE category_id = :categoryId ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question getRandomQuestionByCategory(@Param("categoryId") Long categoryId);

    // RANDOM QUESTION BY LOCALE
    @Query(value = "SELECT * FROM question WHERE locale = :locale ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question getRandomQuestionByLocale(@Param("locale") String locale);
}
