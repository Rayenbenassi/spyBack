package com.game.spy.v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Vote")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Add this
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    @JsonBackReference("vote-round") // Consistent naming
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id")
    @JsonIgnoreProperties({"session", "rounds"}) // Prevent circular references
    private Player voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voted_for_id")
    @JsonIgnoreProperties({"session", "rounds"}) // Prevent circular references
    private Player votedFor;
}