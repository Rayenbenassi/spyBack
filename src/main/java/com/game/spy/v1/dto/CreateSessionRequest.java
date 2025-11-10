package com.game.spy.v1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSessionRequest {
    private List<String> playerNames;
}
