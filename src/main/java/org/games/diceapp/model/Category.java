package org.games.diceapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Category {
    ONES {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 1);
        }
    },
    TWOS {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 2);
        }
    },
    THREES {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 3);
        }
    },
    FOURS {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 4);
        }
    },
    FIVES {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 5);
        }
    },
    SIXES {
        @Override
        public int calculateScore(List<Integer> dice) {
            return countNumOccurrances(dice, 6);
        }
    },
    THREE_OF_A_KIND {
        @Override
        public int calculateScore(List<Integer> dice) {
            Map<Integer, Long> counts = getCounts(dice);
            return counts.containsValue(3L) || counts.containsValue(4L) || counts.containsValue(5L) ? getSum(dice) : 0;
        }
    },
    FOUR_OF_A_KIND {
        @Override
        public int calculateScore(List<Integer> dice) {
            Map<Integer, Long> counts = getCounts(dice);
            return counts.containsValue(4L) || counts.containsValue(5L) ? getSum(dice) : 0;
        }
    },
    FULL_HOUSE {
        @Override
        public int calculateScore(List<Integer> dice) {
            Map<Integer, Long> counts = getCounts(dice);
            return (counts.containsValue(3L) && counts.containsValue(2L)) ? 25 : 0;
        }
    },
    SMALL_STRAIGHT {
        @Override
        public int calculateScore(List<Integer> dice) {
            List<Integer> distances = getDistances(dice);
            return Collections.indexOfSubList(distances, List.of(1, 1, 1)) != -1 ? 30 : 0;
        }
    },
    LARGE_STRAIGHT {
        @Override
        public int calculateScore(List<Integer> dice) {
            List<Integer> distances = getDistances(dice);
            return Collections.indexOfSubList(distances, List.of(1, 1, 1, 1)) != -1 ? 40 : 0;
        }
    },
    POKER {
        @Override
        public int calculateScore(List<Integer> dice) {
            Map<Integer, Long> counts = getCounts(dice);
            return counts.containsValue(5L) ? 50 : 0;
        }
    },
    CHANCE {
        @Override
        public int calculateScore(List<Integer> dice) {
            return getSum(dice);
        }
    };

    public static List<Category> getCategories() {
        return List.of(values());
    }

    public abstract int calculateScore(List<Integer> dice);

    private static Integer countNumOccurrances(List<Integer> dice, Integer num) {
        return dice.stream().filter(i -> i.equals(num)).mapToInt(Integer::intValue).sum();
    }

    private static Map<Integer, Long> getCounts(List<Integer> dice) {
        return dice.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    private static Integer getSum(List<Integer> dice) {
        return dice.stream().mapToInt(Integer::intValue).sum();
    }

    private static List<Integer> getDistances(List<Integer> dice) {
        List<Integer> inputCopy = new ArrayList<>(dice);
        Collections.sort(inputCopy);
        Collections.reverse(inputCopy);
        List<Integer> distances = new ArrayList<>();
        for (int i = 0; i < inputCopy.size() - 1; i++) {
            if (inputCopy.get(i) - inputCopy.get(i + 1) != 0) {
                distances.add(inputCopy.get(i) - inputCopy.get(i + 1));
            }
        }
        return distances;
    }

}
