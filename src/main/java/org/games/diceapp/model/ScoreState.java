package org.games.diceapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreState {
    private int score;
    private List<Integer> sortedUsedRollIndexes;
    private List<Integer> usedRollIndexes;

    public static ScoreState empty() {
        return new ScoreState(0, List.of(), List.of());
    }

    public ScoreState withNextRoll(int rollIndex, int points) {
        List<Integer> newUnsortedVisitedRollIndexes = new ArrayList<>(usedRollIndexes);
        newUnsortedVisitedRollIndexes.add(rollIndex);

        List<Integer> newSortedVisitedRollIndexes = new ArrayList<>(newUnsortedVisitedRollIndexes);
        Collections.sort(newSortedVisitedRollIndexes);

        return new ScoreState(
                score + points,
                newSortedVisitedRollIndexes,
                newUnsortedVisitedRollIndexes
        );
    }
}
