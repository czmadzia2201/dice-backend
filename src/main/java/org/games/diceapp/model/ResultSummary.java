package org.games.diceapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultSummary {
    private Integer totalScore;
    private List<RollEntry> rollHistory;
}
