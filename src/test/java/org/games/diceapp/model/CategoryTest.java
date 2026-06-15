package org.games.diceapp.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    void calculateScoreForNumberCategories() {
        // Test for ONES category
        assertEquals(2, Category.ONES.calculateScore(List.of(1, 1, 2, 3, 4)));
        assertEquals(0, Category.ONES.calculateScore(List.of(2, 3, 4, 5, 6)));

        // Test for TWOS category
        assertEquals(4, Category.TWOS.calculateScore(List.of(2, 1, 3, 2, 4)));
        assertEquals(0, Category.TWOS.calculateScore(List.of(1, 3, 4, 5, 6)));

        // Test for THREES category
        assertEquals(9, Category.THREES.calculateScore(List.of(3, 1, 3, 1, 3)));
        assertEquals(0, Category.THREES.calculateScore(List.of(1, 2, 4, 5, 6)));

        // Test for FOURS category
        assertEquals(8, Category.FOURS.calculateScore(List.of(4, 4, 1, 2, 3)));
        assertEquals(0, Category.FOURS.calculateScore(List.of(1, 2, 3, 5, 6)));

        // Test for FIVES category
        assertEquals(10, Category.FIVES.calculateScore(List.of(5, 1, 2, 5, 3)));
        assertEquals(0, Category.FIVES.calculateScore(List.of(1, 2, 3, 4, 6)));

        // Test for SIXES category
        assertEquals(12, Category.SIXES.calculateScore(List.of(1, 2, 6, 6, 3)));
        assertEquals(0, Category.SIXES.calculateScore(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    void calculateScoreForThreeOfAKind() {
        assertEquals(12, Category.THREE_OF_A_KIND.calculateScore(List.of(1, 3, 2, 3, 3)));
        assertEquals(13, Category.THREE_OF_A_KIND.calculateScore(List.of(3, 3, 3, 1, 3)));
        assertEquals(14, Category.THREE_OF_A_KIND.calculateScore(List.of(4, 2, 2, 4, 2)));
        assertEquals(5, Category.THREE_OF_A_KIND.calculateScore(List.of(1, 1, 1, 1, 1)));
        assertEquals(0, Category.THREE_OF_A_KIND.calculateScore(List.of(1, 3, 3, 4, 5)));
    }

    @Test
    void calculateScoreForFourOfAKind() {
        assertEquals(17, Category.FOUR_OF_A_KIND.calculateScore(List.of(4, 4, 4, 4, 1)));
        assertEquals(17, Category.FOUR_OF_A_KIND.calculateScore(List.of(4, 1, 4, 4, 4)));
        assertEquals(10, Category.FOUR_OF_A_KIND.calculateScore(List.of(2, 2, 2, 2, 2)));
        assertEquals(0, Category.FOUR_OF_A_KIND.calculateScore(List.of(4, 1, 4, 5, 4)));
        assertEquals(0, Category.FOUR_OF_A_KIND.calculateScore(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    void calculateScoreForFullHouse() {
        assertEquals(25, Category.FULL_HOUSE.calculateScore(List.of(2, 2, 3, 3, 3)));
        assertEquals(25, Category.FULL_HOUSE.calculateScore(List.of(2, 2, 3, 3, 2)));
        assertEquals(0, Category.FULL_HOUSE.calculateScore(List.of(1, 2, 3, 4, 5)));
        assertEquals(0, Category.FULL_HOUSE.calculateScore(List.of(4, 4, 4, 4, 4)));
        assertEquals(0, Category.FULL_HOUSE.calculateScore(List.of(4, 4, 5, 4, 4)));
        assertEquals(0, Category.FULL_HOUSE.calculateScore(List.of(4, 4, 5, 4, 2)));
    }

    @Test
    void calculateScoreForSmallStraight() {
        assertEquals(30, Category.SMALL_STRAIGHT.calculateScore(List.of(2, 5, 4, 3, 1)));
        assertEquals(30, Category.SMALL_STRAIGHT.calculateScore(List.of(2, 5, 4, 3, 2)));
        assertEquals(0, Category.SMALL_STRAIGHT.calculateScore(List.of(1, 2, 3, 5, 6)));
    }

    @Test
    void calculateScoreForLargeStraight() {
        assertEquals(40, Category.LARGE_STRAIGHT.calculateScore(List.of(2, 1, 5, 4, 3)));
        assertEquals(40, Category.LARGE_STRAIGHT.calculateScore(List.of(2, 5, 4, 6, 3)));
        assertEquals(0, Category.LARGE_STRAIGHT.calculateScore(List.of(1, 2, 3, 4, 6)));
    }

    @Test
    void calculateScoreForYatzy() {
        assertEquals(50, Category.POKER.calculateScore(List.of(2, 2, 2, 2, 2)));
        assertEquals(50, Category.POKER.calculateScore(List.of(6, 6, 6, 6, 6)));
        assertEquals(0, Category.POKER.calculateScore(List.of(1, 2, 3, 4, 5)));
        assertEquals(0, Category.POKER.calculateScore(List.of(1, 1, 1, 1, 2)));
    }

    @Test
    void calculateScoreForChance() {
        assertEquals(15, Category.CHANCE.calculateScore(List.of(2, 1, 5, 4, 3)));
        assertEquals(20, Category.CHANCE.calculateScore(List.of(2, 5, 4, 6, 3)));
        assertEquals(16, Category.CHANCE.calculateScore(List.of(1, 2, 3, 4, 6)));
    }

    @Test
    void scoreSameRollByDifferentCategories() {
        List<Integer> roll = List.of(6, 6, 6, 6, 6);
        assertEquals(30, Category.SIXES.calculateScore(roll));
        assertEquals(30, Category.THREE_OF_A_KIND.calculateScore(roll));
        assertEquals(30, Category.FOUR_OF_A_KIND.calculateScore(roll));
        assertEquals(30, Category.CHANCE.calculateScore(roll));
        assertEquals(50, Category.POKER.calculateScore(roll));
    }

}
