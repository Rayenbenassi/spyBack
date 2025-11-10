package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ManyToAny;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player")
public class Player extends BaseEntity {


    @Column(name = "player_name" ,length = 20,nullable = false)
    private String name;

    private int score;

    //each player belongs to a game session
    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private GameSession session;
}
