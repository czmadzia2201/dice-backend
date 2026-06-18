package org.games.diceapp.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.games.diceapp.util.DiceUtils;
import org.games.diceapp.model.*;
import org.games.diceapp.repository.GameStateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DiceService {

    @Autowired
    private GameStateStore gameStateStore;

    @Autowired
    private OptimalResultService optimalResultService;

    public GameState newGame() {
        return gameStateStore.createNewGame();
    }

    public GameState rollDice(UUID gameId) {
        GameState gameState = gameId == null ?
                gameStateStore.createNewGame() :
                gameStateStore.get(gameId);
        gameState.setCurrentRoll(generateDiceValues());
        gameState.setGamePhase(GamePhase.CHOICE);
        return gameState;
    }

    public GameState scoreCategory(UUID gameId, Category category) {
        GameState gameState = gameStateStore.get(gameId);
        gameState.getAvailableCategories().remove(category);
        int score = category.calculateScore(gameState.getCurrentRoll());
        gameState.setScore(score);
        gameState.setTotalScore(gameState.getTotalScore() == null ? score : gameState.getTotalScore() + score);
        gameState.setGamePhase(gameState.getAvailableCategories().isEmpty() ?
                        GamePhase.FINISHED : GamePhase.ROLL);
        RollEntry rollEntry = new RollEntry(gameState.getRollHistory().size() + 1,
                            gameState.getCurrentRoll().stream().toList(), category, score);
        gameState.getRollHistory().add(rollEntry);
        return gameState;
    }

    public GameState optimalScore(UUID gameId) {
        GameState gameState = gameStateStore.get(gameId);
        gameState.setOptimalResult(generateOptimalResult(gameState));
        return gameState;
    }

    public GameState initManualGame(UUID gameId) {
        GameState gameState = gameStateStore.get(gameId);
        gameState.setManualResult(new ResultSummary(0, DiceUtils.generateEmptyManualHistory()));
        gameState.setManualAvailableCategories(EnumSet.allOf(Category.class));
        return gameState;
    }

    public GameState manualScore(UUID gameId, ManualChoice choice) {
        GameState gameState = gameStateStore.get(gameId);
        choice.action().updateGameState(gameState, choice);
        return gameState;
    }

    private List<Integer> generateDiceValues() {
        List<Integer> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            result.add(random.nextInt(7 - 1) + 1);
        }
        return result;
    }

    private ResultSummary generateOptimalResult(GameState gameState) {
        List<List<Integer>> rollsInGame = gameState.getRollHistory().stream()
                .map(RollEntry::diceValues)
                .toList();
        ScoreState best = optimalResultService.bestScore(rollsInGame);
        List<Integer> order = best.getUsedRollIndexes();
        int optimalTotalScore = best.getScore();
        List<RollEntry> optimalHistory = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            Category category = Category.values()[i];
            List<Integer> diceValues = gameState.getRollHistory().get(order.get(i)).diceValues();
            int score = category.calculateScore(diceValues);
            optimalHistory.add(new RollEntry(order.get(i) + 1, diceValues, category, score));
        }
        optimalHistory = optimalHistory.stream()
                .sorted(Comparator.comparing(RollEntry::rollNumber))
                .toList();
        return new ResultSummary(optimalTotalScore, optimalHistory);
    }

}
