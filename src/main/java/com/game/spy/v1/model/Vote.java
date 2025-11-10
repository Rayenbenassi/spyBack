package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Vote")
public class Vote extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "round_id")
    @JsonBackReference(value = "round-votes")

    private Round round;

    @ManyToOne
    @JoinColumn(name = "voter_id")

    private Player voter;

    @ManyToOne
    @JoinColumn(name = "voted_for_id")
    private Player votedFor;


}
