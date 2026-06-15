package org.games.diceapp.repository;

import org.games.diceapp.model.GameState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStateStore {

    private final Map<UUID, GameState> games = new ConcurrentHashMap<>();

    public GameState createNewGame() {
        GameState gameState = new GameState();
        games.put(gameState.getId(), gameState);
        cleanup();
        return gameState;
    }

    public GameState get(UUID gameId) {
        return games.get(gameId);
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