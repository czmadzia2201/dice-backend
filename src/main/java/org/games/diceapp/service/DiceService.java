package org.games.diceapp.service;

import lombok.RequiredArgsConstructor;
import org.games.diceapp.exception.CategoryNotAvailableException;
import org.games.diceapp.util.DiceUtils;
import org.games.diceapp.model.*;
import org.games.diceapp.repository.GameStateStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiceService {

    private final GameStateStore gameStateStore;

    private final OptimalResultService optimalResultService;

    public GameState newGame(RollMode rollMode) {
        return gameStateStore.createNewGame(rollMode);
    }

    public GameState newGameAndRollDice(RollMode rollMode) {
        GameState gameState = gameStateStore.createNewGame(rollMode);
        return setGameStateValuesOnRoll(gameState, Arrays.asList(true, true, true, true, true));
    }

    public GameState rollDice(UUID gameId, List<Boolean> diceToRoll) {
        GameState gameState = gameStateStore.get(gameId);
        checkOperation(canRoll(gameState));
        return setGameStateValuesOnRoll(gameState, diceToRoll);
    }

    private GameState setGameStateValuesOnRoll(GameState gameState, List<Boolean> diceToRoll) {
        gameState.setCurrentRoll(generateDiceValues(diceToRoll, gameState));
        gameState.setGamePhase(GamePhase.CHOICE);
        gameState.setRollNumberInTurn(nextRollNumberInTurn(gameState));
        return gameState;
    }

    public GameState scoreCategory(UUID gameId, Category category) {
        GameState gameState = gameStateStore.get(gameId);
        checkOperation(canChoose(gameState));
        if (!gameState.getAvailableCategories().contains(category)) {
            throw new CategoryNotAvailableException(gameId, category);
        }
        gameState.getAvailableCategories().remove(category);
        int score = category.calculateScore(gameState.getCurrentRoll());
        gameState.setScore(score);
        gameState.setTotalScore(gameState.getTotalScore() == null ? score : gameState.getTotalScore() + score);
        gameState.setGamePhase(gameState.getAvailableCategories().isEmpty() ?
                        GamePhase.FINISHED : GamePhase.ROLL);
        RollEntry rollEntry = new RollEntry(gameState.getRollHistory().size() + 1,
                            gameState.getCurrentRoll().stream().toList(), category, score);
        gameState.getRollHistory().add(rollEntry);
        gameState.setRollNumberInTurn(0);
        return gameState;
    }

    public GameState optimalScore(UUID gameId) {
        GameState gameState = gameStateStore.get(gameId);
        checkOperation(canPlayExtraOptions(gameState));
        gameState.setOptimalResult(generateOptimalResult(gameState));
        return gameState;
    }

    public GameState initManualGame(UUID gameId) {
        GameState gameState = gameStateStore.get(gameId);
        checkOperation(canPlayExtraOptions(gameState));
        gameState.setManualResult(new ResultSummary(0, DiceUtils.generateEmptyManualHistory()));
        gameState.setManualAvailableCategories(EnumSet.allOf(Category.class));
        return gameState;
    }

    public GameState manualScore(UUID gameId, ManualChoice choice) {
        GameState gameState = gameStateStore.get(gameId);
        checkOperation(canPlayExtraOptions(gameState));
        choice.action().updateGameState(gameState, choice);
        return gameState;
    }

    private List<Integer> generateDiceValues(List<Boolean> diceToRoll, GameState gameState) {
        if (diceToRoll.size() != 5) {
            throw new IllegalArgumentException("DiceToRoll must contain exactly 5 values.");
        }
        if (diceToRoll.contains(null)) {
            throw new IllegalArgumentException("DiceToRoll must not contain null elements.");
        }
        if (gameState.getRollNumberInTurn() == 0 && diceToRoll.contains(false)) {
            throw new IllegalArgumentException("All dice must be rolled on the first roll.");
        }
        List<Integer> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            if (diceToRoll.get(i)) {
                result.add(random.nextInt(7 - 1) + 1);
            } else {
                result.add(gameState.getCurrentRoll().get(i));
            }
        }
        return result;
    }

    private int nextRollNumberInTurn(GameState gameState) {
        if (gameState.getRollMode() == RollMode.SINGLE_ROLL) {
            return 1;
        } else {
            return gameState.getRollNumberInTurn() < 3 ?
                    gameState.getRollNumberInTurn() + 1 : gameState.getRollNumberInTurn();
        }
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

    private void checkOperation(boolean actionCheck) {
        if (!actionCheck) {
            throw new IllegalStateException("Action not allowed at this state of the game.");
        }
    }

    private boolean canRoll(GameState gameState) {
        return gameState.getGamePhase() == GamePhase.ROLL ||
                (gameState.getGamePhase() == GamePhase.CHOICE &&
                        gameState.getRollMode() == RollMode.THREE_ROLLS &&
                        gameState.getRollNumberInTurn() < 3);
    }

    private boolean canChoose(GameState gameState) {
        return gameState.getGamePhase() == GamePhase.CHOICE;
    }

    private boolean canPlayExtraOptions(GameState gameState) {
        return gameState.getGamePhase() == GamePhase.FINISHED;
    }

}
