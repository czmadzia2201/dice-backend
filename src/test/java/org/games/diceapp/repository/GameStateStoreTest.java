package org.games.diceapp.repository;

import org.games.diceapp.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameStateStoreTest {

    private GameStateStore gameStateStore;

    private final UUID gameId1 = UUID.fromString("9fff1a10-b5cc-443b-a5a1-e083ac2bfc86");
    private final UUID gameId2 = UUID.fromString("90a7d758-f9fb-4385-9da4-178ad6d3f490");
    private final UUID gameId3 = UUID.fromString("210ed747-1519-495a-9c49-09ed1c9437b4");

    @BeforeEach
    void setUp() {
        gameStateStore = new GameStateStore();
        gameStateStore.save(createGameState(gameId1, Duration.ofDays(2)));
        gameStateStore.save(createGameState(gameId2, Duration.ofDays(3)));
        gameStateStore.save(createGameState(gameId3, Duration.ofHours(12)));
    }

    @Test
    void shouldCreateNewGameAndRemoveOldGames() {
        GameState gameState = gameStateStore.createNewGame();
        assertNull(gameStateStore.get(gameId1));
        assertNull(gameStateStore.get(gameId2));
        assertNotNull(gameStateStore.get(gameId3));
        assertNotNull(gameState);
        assertNotNull(gameState.getId());
        assertNotNull(gameStateStore.get(gameState.getId()));
    }

    private GameState createGameState(UUID gameId, Duration age) {
        GameState gameState = new GameState();
        gameState.setId(gameId);
        gameState.setCreatedAt(Instant.now().minus(age));
        return gameState;
    }

}