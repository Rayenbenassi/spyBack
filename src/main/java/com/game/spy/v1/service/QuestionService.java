package com.game.spy.v1.service;

import com.game.spy.v1.model.Question;

public interface QuestionService {
    Question getRandomQuestion();
    Question getRandomQuestionByLocale(String locale);
    Question saveQuestion(Question question);
    Question getRandomQuestionByCategory(Long categoryId);

}
