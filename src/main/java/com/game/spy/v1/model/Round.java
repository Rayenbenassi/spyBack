package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "round") // Changed to lowercase (recommended)
public class Round extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference(value = "session-rounds")
    private GameSession session;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Type(JsonBinaryType.class) // ADD THIS ANNOTATION
    @Column(columnDefinition = "jsonb")
    private String spyData;

    @ManyToOne
    @JoinColumn(name = "spy_id")
    private Player spy;

    private int roundNumber;
    private boolean completed = false;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();
}