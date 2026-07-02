package org.games.diceapp.repository;

import org.games.diceapp.exception.GameNotFoundException;
import org.games.diceapp.model.GameState;
import org.games.diceapp.model.RollMode;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStateStore {

    private final Map<UUID, GameState> games = new ConcurrentHashMap<>();

    public GameState createNewGame(RollMode rollMode) {
        GameState gameState = new GameState(rollMode);
        games.put(gameState.getId(), gameState);
        cleanup();
        return gameState;
    }

    public GameState get(UUID gameId) {
        GameState gamestate = games.get(gameId);
        if (gamestate == null) {
            throw new GameNotFoundException(gameId);
        }
        return gamestate;
    }

    public void save(GameState gameState) {
        games.put(gameState.getId(), gameState);
    }

    private void cleanup() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(1));
        games.entrySet().removeIf(entry ->
                entry.getValue().getCreatedAt().isBefore(cutoff)
        );
    }
}