package org.games.diceapp.service;

import org.games.diceapp.model.*;
import org.games.diceapp.repository.GameStateStore;
import org.games.diceapp.util.DiceUtils;
import org.games.diceapp.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DiceServiceTest {

    @Autowired
    private DiceService diceService;

    private final Map<String, UUID> gameIds = new HashMap<>();

    @BeforeEach
    void setUp() throws Exception {
        GameStateStore gameStateStore = new GameStateStore();
        List<String> inputJsons = List.of("gameState_01.json", "gameState_02.json",
                "gameState_03.json", "gameState_04.json", "gameState_05.json");
        for (String inputJson : inputJsons) {
            GameState gameState = TestUtils.loadGameStateFromJson(inputJson);
            gameStateStore.save(gameState);
            gameIds.put(inputJson, gameState.getId());
        }
        diceService = new DiceService(gameStateStore, new OptimalResultService()
        );
    }

    @Test
    void newGame_shouldResetGameState() {
        GameState gameState = diceService.newGame(RollMode.SINGLE_ROLL);

        assertEquals(new ArrayList<>(), gameState.getCurrentRoll());
        assertEquals(GamePhase.ROLL, gameState.getGamePhase());
        assertEquals(EnumSet.allOf(Category.class), gameState.getAvailableCategories());
        assertEquals(new ArrayList<>(), gameState.getRollHistory());
        assertEquals(0, gameState.getRollNumberInTurn());
        assertEquals(RollMode.SINGLE_ROLL, gameState.getRollMode());
        assertNull(gameState.getTotalScore());
        assertNull(gameState.getScore());
    }

    @Test
    void rollDice_shouldGenerateNewRoll_newGame() {
        GameState gameState = diceService.newGameAndRollDice(RollMode.SINGLE_ROLL);

        assertEquals(5, gameState.getCurrentRoll().size());
        assertTrue(gameState.getCurrentRoll().stream()
                .allMatch(value -> value >= 1 && value <= 6));
        assertEquals(GamePhase.CHOICE, gameState.getGamePhase());
        assertEquals(EnumSet.allOf(Category.class), gameState.getAvailableCategories());
        assertEquals(new ArrayList<>(), gameState.getRollHistory());
        assertEquals(1, gameState.getRollNumberInTurn());
        assertEquals(RollMode.SINGLE_ROLL, gameState.getRollMode());
        assertNull(gameState.getTotalScore());
        assertNull(gameState.getScore());
    }

    @Test
    void rollDice_shouldGenerateNewRoll_existingGame() {
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);
        GameState gameState = diceService.rollDice(gameIds.get("gameState_01.json"), diceToRoll);

        assertEquals(5, gameState.getCurrentRoll().size());
        assertTrue(gameState.getCurrentRoll().stream()
                .allMatch(value -> value >= 1 && value <= 6));
        assertEquals(GamePhase.CHOICE, gameState.getGamePhase());
        assertEquals(2, gameState.getRollNumberInTurn());
    }

    @Test
    void rollDice_shouldGenerateNewRoll_rollOnlySelectedDice() {
        List<Boolean> diceToRoll = List.of(false, false, true, false, false);
        GameState gameState = diceService.rollDice(gameIds.get("gameState_01.json"), diceToRoll);

        assertEquals(3, gameState.getCurrentRoll().get(0));
        assertEquals(1, gameState.getCurrentRoll().get(1));
        assertEquals(5, gameState.getCurrentRoll().get(3));
        assertEquals(5, gameState.getCurrentRoll().get(4));
    }

    @Test
    void scoreCategory_shouldUpdateGameState() {
        GameState gameState = diceService.scoreCategory(gameIds.get("gameState_02.json"), Category.LARGE_STRAIGHT);

        assertFalse(gameState.getAvailableCategories().contains(Category.LARGE_STRAIGHT));
        assertEquals(40, gameState.getScore());
        assertEquals(140, gameState.getTotalScore());
        assertEquals(GamePhase.ROLL, gameState.getGamePhase());
        assertEquals(10, gameState.getRollHistory().size());
        RollEntry lastEntry = gameState.getRollHistory().get(gameState.getRollHistory().size() - 1);
        assertEquals(10, lastEntry.rollNumber());
        assertEquals(List.of(5, 1, 3, 2, 4), lastEntry.diceValues());
        assertEquals(Category.LARGE_STRAIGHT, lastEntry.category());
        assertEquals(40, lastEntry.score());
        assertEquals(0, gameState.getRollNumberInTurn());
    }

    @Test
    void scoreCategory_shouldUpdateGameStateToFinished() {
        GameState gameState = diceService.scoreCategory(gameIds.get("gameState_03.json"), Category.SIXES);

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(6, gameState.getScore());
        assertEquals(146, gameState.getTotalScore());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertEquals(13, gameState.getRollHistory().size());
        RollEntry lastEntry = gameState.getRollHistory().get(gameState.getRollHistory().size() - 1);
        assertEquals(13, lastEntry.rollNumber());
        assertEquals(List.of(2, 2, 6, 5, 1), lastEntry.diceValues());
        assertEquals(Category.SIXES, lastEntry.category());
        assertEquals(6, lastEntry.score());
    }

    @Test
    void scoreCategory_shouldCalculateOptimalScore() {
        GameState gameState = diceService.optimalScore(gameIds.get("gameState_04.json"));

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(6, gameState.getScore());
        assertEquals(146, gameState.getTotalScore());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertEquals(13, gameState.getRollHistory().size());
        assertNotNull(gameState.getOptimalResult());
        assertEquals(148, gameState.getOptimalResult().getTotalScore());
        assertEquals(13, gameState.getOptimalResult().getRollHistory().size());
    }

    @Test
    void scoreCategory_shouldInitManualGame() {
        GameState gameState = diceService.initManualGame(gameIds.get("gameState_04.json"));

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertNotNull(gameState.getManualResult());
        assertNotNull(gameState.getManualAvailableCategories());
        assertEquals(EnumSet.allOf(Category.class), gameState.getManualAvailableCategories());
        assertEquals(new ResultSummary(0, DiceUtils.generateEmptyManualHistory()), gameState.getManualResult());
    }

    @Test
    void scoreCategory_shouldAssignManualGameStateEntry() {
        ManualChoice choice = new ManualChoice(11, Category.CHANCE, ChoiceAction.ASSIGN);
        GameState gameState = diceService.manualScore(gameIds.get("gameState_05.json"), choice);

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertEquals(90, gameState.getManualResult().getTotalScore());
        assertFalse(gameState.getManualAvailableCategories().contains(Category.CHANCE));
        assertEquals(9, gameState.getManualAvailableCategories().size());
        assertEquals(new RollEntry(11, List.of(5, 6, 6, 3, 3), Category.CHANCE, 23),
                gameState.getManualResult().getRollHistory().get(10));
    }

    @Test
    void scoreCategory_shouldClearManualGameStateEntry() {
        ManualChoice choice = new ManualChoice(12, Category.SIXES, ChoiceAction.CLEAR);
        GameState gameState = diceService.manualScore(gameIds.get("gameState_05.json"), choice);

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertEquals(55, gameState.getManualResult().getTotalScore());
        assertTrue(gameState.getManualAvailableCategories().contains(Category.SIXES));
        assertEquals(11, gameState.getManualAvailableCategories().size());
        assertEquals(new RollEntry(12, List.of(), null, null),
                gameState.getManualResult().getRollHistory().get(11));
        assertEquals(13, gameState.getManualResult().getRollHistory().size());
    }
}