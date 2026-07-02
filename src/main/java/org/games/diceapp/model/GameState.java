package org.games.diceapp.model;

import lombok.Data;

import java.time.Instant;
import java.util.*;

@Data
public class GameState {
    private UUID id;
    private Instant createdAt;
    private List<Integer> currentRoll;
    private int rollNumberInTurn;
    private RollMode rollMode;
    private Set<Category> availableCategories;
    private Set<Category> manualAvailableCategories;
    private Integer score;
    private Integer totalScore;
    private List<RollEntry> rollHistory;
    private GamePhase gamePhase;
    private ResultSummary optimalResult;
    private ResultSummary manualResult;

    public GameState() {
        this(RollMode.SINGLE_ROLL);
    }

    public GameState(RollMode rollMode) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.currentRoll = new ArrayList<>();
        this.availableCategories = EnumSet.allOf(Category.class);
        this.rollHistory = new ArrayList<>();
        this.gamePhase = GamePhase.ROLL;
        this.rollMode = rollMode;
        this.rollNumberInTurn = 0;
    }

}