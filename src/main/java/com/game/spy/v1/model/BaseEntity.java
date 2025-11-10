package com.game.spy.v1.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  id;

    private LocalDateTime createdAt = LocalDateTime.now();
}

/*
| Entity          | Relationships                                               |
| --------------- | ----------------------------------------------------------- |
| **GameSession** | 1→N Players, 1→N Rounds                                     |
| **Player**      | N→1 GameSession                                             |
| **Round**       | N→1 GameSession, N→1 Question, N→1 Liar (Player), 1→N Votes |
| **Vote**        | N→1 Round, N→1 Voter (Player), N→1 VotedFor (Player)        |
* */
