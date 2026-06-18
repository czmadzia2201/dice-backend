package org.games.diceapp.model;

import java.util.List;

public enum ChoiceAction {
    ASSIGN {
        @Override
        public void updateGameState(GameState gameState, ManualChoice choice) {
            List<Integer> diceValues = gameState.getRollHistory().get(choice.rollNumber() - 1).diceValues();
            int score = choice.category().calculateScore(diceValues);
            RollEntry rollEntry = new RollEntry(choice.rollNumber(), diceValues, choice.category(), score);
            gameState.getManualResult().getRollHistory().set(choice.rollNumber() - 1, rollEntry);
            gameState.getManualResult().setTotalScore(gameState.getManualResult().getTotalScore() + score);
            gameState.getManualAvailableCategories().remove(choice.category());
        }
    },
    CLEAR {
        @Override
        public void updateGameState(GameState gameState, ManualChoice choice) {
            RollEntry rollEntry = gameState.getManualResult().getRollHistory().get(choice.rollNumber() - 1);
            gameState.getManualResult().getRollHistory().set(choice.rollNumber() - 1, new RollEntry(choice.rollNumber(), List.of(), null, null));
            gameState.getManualResult().setTotalScore(gameState.getManualResult().getTotalScore() - rollEntry.score());
            gameState.getManualAvailableCategories().add(rollEntry.category());
        }
    };

    public abstract void updateGameState(GameState gameState, ManualChoice choice);

}

