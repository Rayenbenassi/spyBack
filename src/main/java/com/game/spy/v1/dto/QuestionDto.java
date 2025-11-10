package com.game.spy.v1.dto;

import com.game.spy.v1.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private long id;

    private String text;

    //spy question
    private String altText;
    private String category;
    private String locale;

    public static  QuestionDto toDto(Question question){
        return QuestionDto.builder()
                .id(question.getId())
                .altText(question.getAltText())
                .category(question.getCategory())
                .locale(question.getLocale())
                .text(question.getText())
                .build();


    }

    public static Question toEntity(QuestionDto dto) {
        Question question = new Question();
        if (dto.getId() != 0) {
            question.setId(dto.getId());
        }
        question.setLocale(dto.getLocale());
        question.setText(dto.getText());
        question.setCategory(dto.getCategory());
        question.setAltText(dto.getAltText());
        return question;
    }
}
