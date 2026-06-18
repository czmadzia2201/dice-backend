package org.games.diceapp.integration;
import org.games.diceapp.model.*;
import org.games.diceapp.repository.GameStateStore;
import org.games.diceapp.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameStateStore gameStateStore;

    @Test
    public void shouldRollDiceForExistingGame() {
        // GIVEN
        ResponseEntity<GameState> newGameResponse = restTemplate.postForEntity("/dice/new-game", null, GameState.class);
        assertThat(newGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID gameId = newGameResponse.getBody().getId();

        // WHEN
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/" + gameId + "/roll", null, GameState.class);

        // THEN
        assertThat(rollResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = rollResponse.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isEqualTo(gameId);
        assertThat(gameState.getCurrentRoll()).isNotEmpty();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
    }

    @Test
    public void shouldCreateNewGameWhenRollingWithoutId() {
        // WHEN
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/roll", null, GameState.class);

        // THEN
        assertThat(rollResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = rollResponse.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
    }

    @Test
    public void shouldScoreCategory() {
        // GIVEN
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/dice/roll", null, GameState.class);
        GameState initialState = rollResponse.getBody();
        UUID gameId = initialState.getId();

        // WHEN
        ResponseEntity<GameState> scoreResponse = restTemplate.postForEntity("/dice/" + gameId + "/score", org.games.diceapp.model.Category.ONES, GameState.class);

        // THEN
        assertThat(scoreResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState updatedState = scoreResponse.getBody();
        assertThat(updatedState).isNotNull();
        assertThat(updatedState.getAvailableCategories()).doesNotContain(org.games.diceapp.model.Category.ONES);
        assertThat(updatedState.getScore()).isNotNull();
        assertThat(updatedState.getRollHistory()).hasSize(1);
    }

    @Test
    public void shouldCreateNewGame() {
        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/new-game", null, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getAvailableCategories()).hasSize(13); // Assuming all 13 categories are available
        assertThat(gameState.getRollHistory()).isEmpty();
    }

    @Test
    public void shouldCalculateOptimalScore() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_04.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/" + gameId + "/optimal", null, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState result = response.getBody();
        assertThat(result.getOptimalResult()).isNotNull();
        assertThat(result.getOptimalResult().getTotalScore()).isEqualTo(148);
        assertThat(result.getOptimalResult().getRollHistory()).hasSize(13);
    }

    @Test
    public void shouldInitManualGame() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_04.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/" + gameId + "/init-manual", null, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState result = response.getBody();
        assertThat(result.getManualResult()).isNotNull();
        assertThat(result.getManualResult().getTotalScore()).isEqualTo(0);
        assertThat(result.getManualResult().getRollHistory()).hasSize(13);
    }

    @Test
    public void shouldAssignManualGameStateEntry() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_05.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);
        ManualChoice choice = new ManualChoice(1, Category.ONES, ChoiceAction.ASSIGN);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/" + gameId + "/manual", choice, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState result = response.getBody();
        assertThat(result.getManualResult()).isNotNull();
        // The initial manual score in the JSON file is 67, after adding ONES (1 p) should be 68
        assertThat(result.getManualResult().getTotalScore()).isEqualTo(68);
        assertThat(result.getManualAvailableCategories()).doesNotContain(Category.ONES);
        assertThat(result.getManualResult().getRollHistory().get(0)).isEqualTo(
                new RollEntry(1, List.of(4, 1, 2, 4, 2), Category.ONES, 1));
    }

    @Test
    public void shouldClearManualGameStateEntry() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_05.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);
        ManualChoice choice = new ManualChoice(12, Category.SIXES, ChoiceAction.CLEAR);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/dice/" + gameId + "/manual", choice, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState result = response.getBody();
        assertThat(result.getManualResult()).isNotNull();
        // The initial manual score in the JSON file is 67, after subtracting SIXES (12 p) should be 55
        assertThat(result.getManualResult().getTotalScore()).isEqualTo(55);
        assertThat(result.getManualAvailableCategories()).contains(Category.SIXES);
        assertThat(result.getManualResult().getRollHistory().get(11)).isEqualTo(
                new RollEntry(12, List.of(), null, null));
    }

}
