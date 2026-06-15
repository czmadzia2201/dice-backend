package org.games.diceapp;

import org.games.diceapp.model.GameState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiceApplication.class, args);
    }

}
