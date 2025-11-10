package com.game.spy.v1.controller;

import com.game.spy.v1.dto.QuestionDto;
import com.game.spy.v1.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/create")
    public QuestionDto createNewQuestion(@RequestBody QuestionDto questionDto){
        return QuestionDto.toDto(
                questionService.saveQuestion(QuestionDto.toEntity(questionDto))
        );
    }

    @GetMapping("/random")
    public QuestionDto getRandomQuestion() {
        return QuestionDto.toDto(questionService.getRandomQuestion());
    }
}
