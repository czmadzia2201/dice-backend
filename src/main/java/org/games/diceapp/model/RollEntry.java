package org.games.diceapp.model;

import java.util.List;

public record RollEntry(int rollNumber, List<Integer> diceValues, Category category, Integer score) {

    public boolean isEmpty() {
        return category == null && score == null;
    }
}
