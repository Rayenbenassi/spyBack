package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player")
public class Player extends BaseEntity {


    @Column(name = "player_name" ,length = 20,nullable = false)
    private String name;

    private int score;

    private Boolean isEliminated = false;

    private Long eliminatedInRoundId = 0L;

    //each player belongs to a game session
    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore
    private GameSession session;
}
