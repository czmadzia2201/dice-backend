package org.games.diceapp.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ManualChoice(
        @Min(1) @Max(13) int rollNumber,
        @NotNull Category category,
        @NotNull ChoiceAction action) {
}
