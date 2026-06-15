package org.games.diceapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.games.diceapp.model.*;
import org.games.diceapp.repository.GameStateStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

public class DiceServiceTest {

    @Autowired
    private DiceService diceService;

    @BeforeEach
    void setUp() throws Exception {
        GameStateStore gameStateStore = new GameStateStore();
        gameStateStore.save(loadGameStateFromJson("gameState_01.json"));
        gameStateStore.save(loadGameStateFromJson("gameState_02.json"));
        gameStateStore.save(loadGameStateFromJson("gameState_03.json"));
        gameStateStore.save(loadGameStateFromJson("gameState_04.json"));
        diceService = new DiceService(
                gameStateStore,
                new OptimalResultService()
        );
    }

    @Test
    void newGame_shouldResetGameState() throws Exception {
        GameState gameState = diceService.newGame();

        assertEquals(new ArrayList<>(), gameState.getCurrentRoll());
        assertEquals(GamePhase.ROLL, gameState.getGamePhase());
        assertEquals(EnumSet.allOf(Category.class), gameState.getAvailableCategories());
        assertEquals(new ArrayList<>(), gameState.getRollHistory());
        assertNull(gameState.getTotalScore());
        assertNull(gameState.getScore());
    }

    @Test
    void rollDice_shouldGenerateNewRoll_newGame() {
        GameState gameState = diceService.rollDice(null);

        assertEquals(5, gameState.getCurrentRoll().size());
        assertTrue(gameState.getCurrentRoll().stream()
                .allMatch(value -> value >= 1 && value <= 6));
        assertEquals(GamePhase.CHOICE, gameState.getGamePhase());
        assertEquals(EnumSet.allOf(Category.class), gameState.getAvailableCategories());
        assertEquals(new ArrayList<>(), gameState.getRollHistory());
        assertNull(gameState.getTotalScore());
        assertNull(gameState.getScore());
    }

    @Test
    void rollDice_shouldGenerateNewRoll() {
        GameState gameState = diceService.rollDice(UUID.fromString("9fff1a10-b5cc-443b-a5a1-e083ac2bfc86"));

        assertEquals(5, gameState.getCurrentRoll().size());
        assertTrue(gameState.getCurrentRoll().stream()
                .allMatch(value -> value >= 1 && value <= 6));
        assertEquals(GamePhase.CHOICE, gameState.getGamePhase());
    }

    @Test
    void scoreCategory_shouldUpdateGameState() throws Exception {
        GameState gameState = diceService.scoreCategory(
                UUID.fromString("90a7d758-f9fb-4385-9da4-178ad6d3f490"), Category.LARGE_STRAIGHT);

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
    }

    @Test
    void scoreCategory_shouldUpdateGameStateToFinished() throws Exception {
        GameState gameState = diceService.scoreCategory(
                UUID.fromString("210ed747-1519-495a-9c49-09ed1c9437b4"), Category.SIXES);

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
    void scoreCategory_shouldCalculateOptimalScore() throws Exception {
        GameState gameState = diceService.optimalScore(UUID.fromString("b47e259d-7780-4785-be41-1fe6a9bfc667"));

        assertTrue(gameState.getAvailableCategories().isEmpty());
        assertEquals(6, gameState.getScore());
        assertEquals(146, gameState.getTotalScore());
        assertEquals(GamePhase.FINISHED, gameState.getGamePhase());
        assertEquals(13, gameState.getRollHistory().size());
        assertNotNull(gameState.getOptimalResult());
        assertEquals(148, gameState.getOptimalResult().getTotalScore());
        assertEquals(13, gameState.getOptimalResult().getRollHistory().size());
    }

    private GameState loadGameStateFromJson(String fileName) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(format("File %s not found", fileName));
        }
        return objectMapper.readValue(resource, GameState.class);
    }

}