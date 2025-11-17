package com.game.spy.v1.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSessionRequest {
    private List<String> playerNames;


   private  SessionConfigDto sessionConfigDto;
}
