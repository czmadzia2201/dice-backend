package org.games.diceapp.repository;

import org.games.diceapp.exception.GameNotFoundException;
import org.games.diceapp.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameStateStoreTest {

    private GameStateStore gameStateStore;

    private final UUID gameId1 = UUID.fromString("9fff1a10-b5cc-443b-a5a1-e083ac2bfc86");
    private final UUID gameId2 = UUID.fromString("90a7d758-f9fb-4385-9da4-178ad6d3f490");
    private final UUID gameId3 = UUID.fromString("210ed747-1519-495a-9c49-09ed1c9437b4");
    private final UUID gameId4 = UUID.fromString("c89592fb-5f4e-40cd-a663-92657430e4a3");

    @BeforeEach
    public void setUp() {
        gameStateStore = new GameStateStore();
        gameStateStore.save(createGameState(gameId1, Duration.ofDays(2)));
        gameStateStore.save(createGameState(gameId2, Duration.ofDays(3)));
        gameStateStore.save(createGameState(gameId3, Duration.ofHours(12)));
    }

    @Test
    public void shouldCreateNewGameAndRemoveOldGames() {
        GameState gameState = gameStateStore.createNewGame();
        assertThrows(GameNotFoundException.class, () -> gameStateStore.get(gameId1));
        assertThrows(GameNotFoundException.class, () -> gameStateStore.get(gameId2));
        assertNotNull(gameStateStore.get(gameId3));
        assertNotNull(gameState);
        assertNotNull(gameState.getId());
        assertNotNull(gameStateStore.get(gameState.getId()));
    }

    @Test
    public void shouldThrowExceptionForNonExistingGame() {
        assertThatThrownBy(() -> gameStateStore.get(gameId4))
                .isInstanceOf(GameNotFoundException.class)
                .hasMessage("Game not found: " + gameId4);
    }

    private GameState createGameState(UUID gameId, Duration age) {
        GameState gameState = new GameState();
        gameState.setId(gameId);
        gameState.setCreatedAt(Instant.now().minus(age));
        return gameState;
    }

}