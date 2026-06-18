package org.games.diceapp.service;

import org.games.diceapp.model.ScoreState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class OptimalResultServiceTest {

    private final OptimalResultService optimalResultService = new OptimalResultService();

    @ParameterizedTest
    @MethodSource("games")
    public void shouldCalculateOptimalScore(List<List<Integer>> rollsInGame, int expectedScore) {
        ScoreState bestScore = optimalResultService.bestScore(rollsInGame);
        assertEquals(expectedScore, bestScore.getScore());
    }

    private static Stream<Arguments> games() {
        return Stream.of(
                Arguments.of(List.of(List.of(6, 3, 5, 6, 5), List.of(3, 5, 6, 3, 6),
                        List.of(1, 6, 3, 3, 3), List.of(3, 3, 2, 1, 4), List.of(3, 2, 3, 5, 2),
                        List.of(5, 5, 5, 6, 4), List.of(1, 5, 6, 4, 4), List.of(4, 6, 3, 3, 3),
                        List.of(2, 2, 4, 5, 5), List.of(6, 5, 6, 2, 1), List.of(1, 4, 1, 4, 6),
                        List.of(1, 1, 6, 6, 3), List.of(6, 2, 5, 2, 4)), 125),
                Arguments.of(List.of(List.of(2, 6, 2, 2, 6), List.of(2, 6, 3, 1, 3),
                        List.of(6, 1, 3, 5, 4), List.of(2, 5, 6, 3, 4), List.of(1, 6, 6, 4, 6),
                        List.of(6, 2, 5, 2, 6), List.of(1, 5, 3, 3, 5), List.of(5, 4, 2, 3, 5),
                        List.of(6, 3, 1, 4, 5), List.of(4, 5, 6, 1, 6), List.of(1, 1, 3, 4, 6),
                        List.of(4, 3, 6, 2, 2), List.of(1, 5, 1, 4, 3)), 178),
                Arguments.of(List.of(List.of(5, 6, 2, 2, 2), List.of(2, 3, 2, 3, 5),
                        List.of(2, 5, 2, 1, 2), List.of(1, 4, 1, 2, 4), List.of(4, 1, 6, 4, 3),
                        List.of(1, 4, 2, 3, 1), List.of(4, 6, 4, 2, 4), List.of(5, 2, 3, 6, 1),
                        List.of(3, 5, 1, 3, 2), List.of(1, 4, 6, 1, 4), List.of(3, 5, 6, 1, 5),
                        List.of(6, 2, 3, 5, 5), List.of(6, 1, 4, 6, 3)), 116),
                Arguments.of(List.of(List.of(2, 4, 2, 2, 1), List.of(3, 2, 3, 3, 5),
                        List.of(5, 4, 3, 3, 1), List.of(4, 1, 2, 4, 2), List.of(5, 4, 4, 5, 1),
                        List.of(6, 6, 2, 4, 3), List.of(2, 5, 6, 4, 5), List.of(2, 3, 3, 4, 1),
                        List.of(1, 4, 6, 6, 3), List.of(2, 6, 4, 1, 4), List.of(1, 6, 4, 4, 5),
                        List.of(4, 5, 2, 3, 1), List.of(5, 4, 4, 5, 2)), 151),
                Arguments.of(List.of(List.of(3, 1, 5, 1, 6), List.of(2, 6, 3, 5, 5),
                        List.of(6, 2, 6, 2, 1), List.of(5, 2, 2, 2, 1), List.of(5, 5, 5, 1, 5),
                        List.of(1, 2, 5, 5, 5), List.of(2, 3, 6, 6, 1), List.of(4, 1, 2, 3, 3),
                        List.of(2, 1, 2, 2, 3), List.of(2, 4, 1, 6, 5), List.of(4, 5, 1, 4, 4),
                        List.of(2, 4, 4, 5, 3), List.of(5, 1, 4, 5, 4)), 139)
        );
    }
}