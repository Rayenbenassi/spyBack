package com.game.spy.v1.service.impl;

import com.game.spy.v1.model.Question;
import com.game.spy.v1.repo.QuestionRepo;
import com.game.spy.v1.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepo questionRepo;

    @Override
    public Question getRandomQuestion() {
        return questionRepo.getRandomQuestion();
    }

    @Override
    public Question getRandomQuestionByLocale(String locale) {
        return questionRepo.getRandomQuestionByLocale(locale);
    }

    @Override
    public Question saveQuestion(Question question) {
        return questionRepo.save(question);
    }
}
