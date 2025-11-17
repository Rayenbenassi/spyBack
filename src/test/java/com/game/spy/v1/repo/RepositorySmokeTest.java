package com.game.spy.v1.repo;


import com.game.spy.v1.model.Question;
import com.game.spy.v1.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepositorySmokeTest {

    @Autowired QuestionRepo questionRepo;

}

