package org.games.diceapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.games.diceapp.model.Category;
import org.games.diceapp.model.GameState;
import org.games.diceapp.model.ManualChoice;
import org.games.diceapp.model.RollMode;
import org.games.diceapp.service.DiceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/dice")
@RequiredArgsConstructor
public class DiceController {

    private final DiceService diceService;

    @PostMapping("/new-game")
    public GameState newGame(@NotNull @RequestBody RollMode rollMode) {
        return diceService.newGame(rollMode);
    }

    @PostMapping("/roll")
    public GameState newGameAndRollDice(@NotNull @RequestBody RollMode rollMode) {
        return diceService.newGameAndRollDice(rollMode);
    }

    @PostMapping("/{id}/roll")
    public GameState rollDice(@PathVariable UUID id, @NotNull @RequestBody List<Boolean> diceToRoll) {
        return diceService.rollDice(id, diceToRoll);
    }

    @PostMapping("/{id}/score")
    public GameState scoreCategory(@PathVariable UUID id, @NotNull @RequestBody Category category) {
        return diceService.scoreCategory(id, category);
    }

    @PostMapping("/{id}/optimal")
    public GameState optimalScore(@PathVariable UUID id) {
        return diceService.optimalScore(id);
    }

    @PostMapping("/{id}/init-manual")
    public GameState initManualGame(@PathVariable UUID id) {
        return diceService.initManualGame(id);
    }

    @PostMapping("/{id}/manual")
    public GameState manualScore(@PathVariable UUID id, @NotNull @Valid @RequestBody ManualChoice choice) {
        return diceService.manualScore(id, choice);
    }

}
