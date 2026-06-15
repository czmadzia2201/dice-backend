package org.games.diceapp.service;

import org.games.diceapp.model.Category;
import org.games.diceapp.model.ScoreState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OptimalResultService {

    public ScoreState bestScore(List<List<Integer>> rollsInGame) {
        List<List<Integer>> matrix = calculateMatrix(rollsInGame);

        List<ScoreState> states = List.of(ScoreState.empty());

        for (int categoryIndex = 0; categoryIndex < matrix.get(0).size(); categoryIndex++) {
            Map<List<Integer>, ScoreState> bestStateByUsedRolls = new HashMap<>();

            for (ScoreState state : states) {
                for (int rollIndex = 0; rollIndex < matrix.size(); rollIndex++) {

                    if (!state.getSortedUsedRollIndexes().contains(rollIndex)) {
                        int points = matrix.get(rollIndex).get(categoryIndex);

                        ScoreState candidate = state.withNextRoll(rollIndex, points);

                        bestStateByUsedRolls.merge(
                                candidate.getSortedUsedRollIndexes(),
                                candidate,
                                (existing, replacement) ->
                                        replacement.getScore() > existing.getScore()
                                                ? replacement
                                                : existing
                        );
                    }
                }
            }

            states = new ArrayList<>(bestStateByUsedRolls.values());
        }

        return states.get(0);
    }

// Original implementation
//    public ScoreState bestScore(List<List<Integer>> rollsInGame) {
//        List<List<Integer>> matrix = calculateMatrix(rollsInGame);
//        List<ScoreState> scores = new ArrayList<>(List.of(ScoreState.empty()));
//        for (int a = 0; a < matrix.get(0).size(); a++) {
//            List<ScoreState> newScores = new ArrayList<>();
//            for (ScoreState score : scores) {
//                for (int i = 0; i < matrix.size(); i++) {
//                    if (!score.getSortedUsedRollIndexes().contains(i)) {
//                        List<Integer> usedIndexes = Stream.concat(score.getUsedRollIndexes().stream(), Stream.of(i)).collect(Collectors.toList());
//                        List<Integer> sortedUsedIndexes = usedIndexes.stream().sorted().collect(Collectors.toList());
//                        ScoreState usedState = findByUsed(newScores, sortedUsedIndexes);
//                        if (usedState == null) {
//                            ScoreState localScore = new ScoreState();
//                            localScore.setScore(score.getScore() + matrix.get(i).get(a));
//                            localScore.setSortedUsedRollIndexes(sortedUsedIndexes);
//                            localScore.setUsedRollIndexes(usedIndexes);
//                            newScores.add(localScore);
//                        } else {
//                            if (usedState.getScore() < score.getScore() + matrix.get(i).get(a)) {
//                                usedState.setScore(score.getScore() + matrix.get(i).get(a));
//                                usedState.setUsedRollIndexes(usedIndexes);
//                            }
//                        }
//                    }
//                }
//            }
//            scores.clear();
//            scores.addAll(newScores);
//        }
//        return scores.get(0);
//    }
//
//    private ScoreState findByUsed(List<ScoreState> scores, List<Integer> usedIndexes) {
//        return scores.stream().filter(i -> i.getSortedUsedRollIndexes().equals(usedIndexes)).findFirst().orElse(null);
//    }
//
    private List<List<Integer>> calculateMatrix(List<List<Integer>> rollsInGame) {
        return rollsInGame.stream().map(this::allResultsForOneRoll).toList();
    }

    private List<Integer> allResultsForOneRoll(List<Integer> diceValues) {
        return Arrays.stream(Category.values())
                .map(category -> category.calculateScore(diceValues))
                .toList();
    }

}
