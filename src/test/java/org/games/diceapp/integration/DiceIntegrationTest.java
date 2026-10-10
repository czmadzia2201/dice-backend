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
class DiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameStateStore gameStateStore;

    @Test
    void shouldCreateNewGame() {
        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/new-game", RollMode.SINGLE_ROLL, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getAvailableCategories()).hasSize(13); // Assuming all 13 categories are available
        assertThat(gameState.getRollHistory()).isEmpty();
        assertThat(gameState.getRollMode()).isEqualTo(RollMode.SINGLE_ROLL);
        assertThat(gameState.getRollNumberInTurn()).isEqualTo(0);
    }

    @Test
    void shouldCreateNewGameWhenRollingWithoutId() {
        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/roll", RollMode.SINGLE_ROLL, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isNotNull();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
        assertThat(gameState.getRollMode()).isEqualTo(RollMode.SINGLE_ROLL);
        assertThat(gameState.getRollNumberInTurn()).isEqualTo(1);
    }

    @Test
    void shouldRollDiceForExistingGame() {
        // GIVEN
        ResponseEntity<GameState> newGameResponse = restTemplate.postForEntity("/new-game", RollMode.SINGLE_ROLL, GameState.class);
        assertThat(newGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID gameId = newGameResponse.getBody().getId();
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getId()).isEqualTo(gameId);
        assertThat(gameState.getCurrentRoll()).isNotEmpty();
        assertThat(gameState.getCurrentRoll()).hasSize(5);
        assertThat(gameState.getRollMode()).isEqualTo(RollMode.SINGLE_ROLL);
        assertThat(gameState.getRollNumberInTurn()).isEqualTo(1);
    }

    @Test
    void shouldRollDiceForExistingGame_rollSelectedDice() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_01.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);
        List<Boolean> diceToRoll = List.of(false, false, false, true, false);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getCurrentRoll().get(0)).isEqualTo(3);
        assertThat(gameState.getCurrentRoll().get(1)).isEqualTo(1);
        assertThat(gameState.getCurrentRoll().get(2)).isEqualTo(3);
        assertThat(gameState.getCurrentRoll().get(4)).isEqualTo(5);
        assertThat(gameState.getRollMode()).isEqualTo(RollMode.THREE_ROLLS);
        assertThat(gameState.getRollNumberInTurn()).isEqualTo(2);
    }

    @Test
    void shouldAllowOneRollForSingleRollMode() {
        // WHEN
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);
        ResponseEntity<GameState> newGameResponse = restTemplate.postForEntity("/new-game", RollMode.SINGLE_ROLL, GameState.class);

        // THEN
        UUID gameId = newGameResponse.getBody().getId();
        assertThat(newGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GameState> response1 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody().getRollNumberInTurn()).isEqualTo(1);

        ResponseEntity<String> response2 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldAllowThreeRollsForThreeRollsMode() {
        // WHEN
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);
        ResponseEntity<GameState> newGameResponse = restTemplate.postForEntity("/new-game", RollMode.THREE_ROLLS, GameState.class);

        // THEN
        UUID gameId = newGameResponse.getBody().getId();

        ResponseEntity<GameState> response1 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody().getRollNumberInTurn()).isEqualTo(1);

        ResponseEntity<GameState> response2 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody().getRollNumberInTurn()).isEqualTo(2);

        ResponseEntity<GameState> response3 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response3.getBody().getRollNumberInTurn()).isEqualTo(3);

        ResponseEntity<String> response4 = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, String.class);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnNotFoundStatusOnGameIdNotFound() {
        // GIVEN
        UUID gameId = UUID.randomUUID();
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);

        // WHEN
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);

        // THEN
        assertThat(rollResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldScoreCategory() {
        // GIVEN
        ResponseEntity<GameState> rollResponse = restTemplate.postForEntity("/roll", RollMode.THREE_ROLLS, GameState.class);
        GameState initialState = rollResponse.getBody();
        UUID gameId = initialState.getId();

        List<Boolean> diceToRoll = List.of(true, true, true, true, true);
        restTemplate.postForEntity("/" + gameId + "/roll", diceToRoll, GameState.class);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/score", Category.ONES, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getAvailableCategories()).doesNotContain(Category.ONES);
        assertThat(gameState.getScore()).isNotNull();
        assertThat(gameState.getTotalScore()).isNotNull();
        assertThat(gameState.getRollHistory()).hasSize(1);
        assertThat(gameState.getRollNumberInTurn()).isEqualTo(0);
    }

    @Test
    void shouldCalculateOptimalScore() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_04.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/optimal", null, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getOptimalResult()).isNotNull();
        assertThat(gameState.getOptimalResult().getTotalScore()).isEqualTo(148);
        assertThat(gameState.getOptimalResult().getRollHistory()).hasSize(13);
    }

    @Test
    void shouldInitManualGame() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_04.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/init-manual", null, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getManualResult()).isNotNull();
        assertThat(gameState.getManualResult().getTotalScore()).isEqualTo(0);
        assertThat(gameState.getManualResult().getRollHistory()).hasSize(13);
    }

    @Test
    void shouldAssignManualGameStateEntry() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_05.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);
        ManualChoice choice = new ManualChoice(1, Category.ONES, ChoiceAction.ASSIGN);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/manual", choice, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getManualResult()).isNotNull();
        // The initial manual score in the JSON file is 67, after adding ONES (1 p) should be 68
        assertThat(gameState.getManualResult().getTotalScore()).isEqualTo(68);
        assertThat(gameState.getManualAvailableCategories()).doesNotContain(Category.ONES);
        assertThat(gameState.getManualResult().getRollHistory().get(0)).isEqualTo(
                new RollEntry(1, List.of(4, 1, 2, 4, 2), Category.ONES, 1));
    }

    @Test
    void shouldClearManualGameStateEntry() throws Exception {
        // GIVEN
        GameState preparedState = TestUtils.loadGameStateFromJson("gameState_05.json");
        UUID gameId = preparedState.getId();
        gameStateStore.save(preparedState);
        ManualChoice choice = new ManualChoice(12, Category.SIXES, ChoiceAction.CLEAR);

        // WHEN
        ResponseEntity<GameState> response = restTemplate.postForEntity("/" + gameId + "/manual", choice, GameState.class);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GameState gameState = response.getBody();
        assertThat(gameState).isNotNull();
        assertThat(gameState.getManualResult()).isNotNull();
        // The initial manual score in the JSON file is 67, after subtracting SIXES (12 p) should be 55
        assertThat(gameState.getManualResult().getTotalScore()).isEqualTo(55);
        assertThat(gameState.getManualAvailableCategories()).contains(Category.SIXES);
        assertThat(gameState.getManualResult().getRollHistory().get(11)).isEqualTo(
                new RollEntry(12, List.of(), null, null));
    }

}
