package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GameSession")
public class GameSession extends BaseEntity {

    private int currentRound = 0;

    private boolean finished = false;

    private Integer numberOfRounds;

    private Category category;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Player> players = new ArrayList<Player>();

    @OneToMany(mappedBy = "session",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference(value = "session-rounds")

    private List<Round> rounds = new ArrayList<Round>();

}
