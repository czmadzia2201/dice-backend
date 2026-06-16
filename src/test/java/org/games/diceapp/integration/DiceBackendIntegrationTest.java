package org.games.diceapp.integration;
import org.games.diceapp.model.GameState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiceBackendIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRollDiceForExistingGame() {
        // Given: create a new game first
        ResponseEntity<GameState> newGameResponse = restTemplate.postForEntity("/dice/new-game", null, GameState.class);
        assertThat(newGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID gameId = newGameResponse.getBody().getId();

        // When: roll dice for that game
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/" + gameId + "/roll", null, GameState.class);

        // Then: check if the response is OK and dice are rolled
        assertThat(rollResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = rollResponse.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isEqualTo(gameId);
        assertThat(gameState.getCurrentRoll()).isNotEmpty();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
    }

    @Test
    void shouldCreateNewGameWhenRollingWithoutId() {
        // When: roll dice without providing an ID
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/roll", null, GameState.class);

        // Then: a new game should be created and dice rolled
        assertThat(rollResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = rollResponse.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
    }
}
