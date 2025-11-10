package com.game.spy.v1.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Builder
@Getter
@Setter
@Table(name = "Question")
@AllArgsConstructor
@NoArgsConstructor
public class Question extends BaseEntity {

    //question text
    private String text;

    //spy question
    private String altText;
    private String category;  // e.g. "food", "culture"
    private String locale;


}
