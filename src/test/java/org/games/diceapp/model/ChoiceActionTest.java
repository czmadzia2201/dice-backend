package org.games.diceapp.model;

import org.games.diceapp.util.DiceUtils;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoiceActionTest {

    @Test
    void testAssignAction() {
        // GIVEN
        GameState gameState = new GameState();
        gameState.setRollHistory(List.of(new RollEntry(1, List.of(6, 3, 3, 6, 6), Category.THREE_OF_A_KIND, 24)));
        gameState.setManualResult(new ResultSummary(30, DiceUtils.generateEmptyManualHistory()));
        gameState.setManualAvailableCategories(EnumSet.allOf(Category.class));
        ManualChoice choice = new ManualChoice(1, Category.FULL_HOUSE, ChoiceAction.ASSIGN);

        // WHEN
        choice.action().updateGameState(gameState, choice);

        // THEN
        assertEquals(55, gameState.getManualResult().getTotalScore());
        assertEquals(new RollEntry(1, List.of(6, 3, 3, 6, 6), Category.FULL_HOUSE, 25),
                gameState.getManualResult().getRollHistory().get(0));
        assertFalse(gameState.getManualAvailableCategories().contains(Category.FULL_HOUSE));
    }

    @Test
    void testClearAction() {
        // GIVEN
        GameState gameState = new GameState();
        gameState.setManualResult(new ResultSummary(50, DiceUtils.generateEmptyManualHistory()));
        gameState.getManualResult().getRollHistory().set(0, new RollEntry(1, List.of(6, 3, 3, 6, 6), Category.FULL_HOUSE, 25));
        gameState.setManualAvailableCategories(EnumSet.allOf(Category.class));
        gameState.getManualAvailableCategories().remove(Category.FULL_HOUSE);
        ManualChoice choice = new ManualChoice(1, Category.FULL_HOUSE, ChoiceAction.CLEAR);

        // WHEN
        choice.action().updateGameState(gameState, choice);

        // THEN
        assertEquals(25,  gameState.getManualResult().getTotalScore());
        assertEquals(new RollEntry(1, List.of(), null, null), gameState.getManualResult().getRollHistory().get(0));
        assertEquals(EnumSet.allOf(Category.class), gameState.getManualAvailableCategories());
    }

}