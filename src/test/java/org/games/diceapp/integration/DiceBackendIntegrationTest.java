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

    @Test
    void shouldScoreCategory() {
        // Given: create a new game and roll dice
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/roll", null, GameState.class);
        GameState initialState = rollResponse.getBody();
        UUID gameId = initialState.getId();

        // When: score a category (e.g., ONES)
        ResponseEntity<GameState> scoreResponse = restTemplate.postForEntity("/dice/" + gameId + "/score", org.games.diceapp.model.Category.ONES, GameState.class);

        // Then: score should be updated and category removed from available
        assertThat(scoreResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState updatedState = scoreResponse.getBody();
        assertThat(updatedState).isNotNull();
        assertThat(updatedState.getAvailableCategories()).doesNotContain(org.games.diceapp.model.Category.ONES);
        assertThat(updatedState.getScore()).isNotNull();
        assertThat(updatedState.getRollHistory()).hasSize(1);
    }

    @Test
    void shouldCreateNewGame() {
        // When: create a new game
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/new-game", null, GameState.class);

        // Then: a new game should be initialized
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getAvailableCategories()).hasSize(13); // Assuming all 13 categories are available
        assertThat(gameState.getRollHistory()).isEmpty();
    }

}
