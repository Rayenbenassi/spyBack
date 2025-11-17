package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Round")
public class Round extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference(value = "session-rounds")

    private GameSession session;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "spy_id")
    private Player spy;

    private int roundNumber;
    private boolean completed = false;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    @JsonManagedReference("vote-round") // Match the back reference
    private List<Vote> votes = new ArrayList<>();


}
