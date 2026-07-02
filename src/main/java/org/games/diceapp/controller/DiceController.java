package org.games.diceapp.controller;

import org.games.diceapp.model.Category;
import org.games.diceapp.model.GameState;
import org.games.diceapp.model.ManualChoice;
import org.games.diceapp.model.RollMode;
import org.games.diceapp.service.DiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dice")
public class DiceController {

    @Autowired
    private DiceService diceService;

    @PostMapping("/{id}/roll")
    public GameState rollDice(@PathVariable UUID id, @RequestBody List<Boolean> diceToRoll) {
        return diceService.rollDice(id, diceToRoll);
    }

    @PostMapping("/roll")
    public GameState newGameAndRollDice(@RequestBody RollMode rollMode) {
        return diceService.newGameAndRollDice(rollMode);
    }

    @PostMapping("/{id}/score")
    public GameState scoreCategory(@PathVariable UUID id, @RequestBody Category category) {
        return diceService.scoreCategory(id, category);
    }

    @PostMapping("/new-game")
    public GameState newGame(@RequestBody RollMode rollMode) {
        return diceService.newGame(rollMode);
    }

    @PostMapping("/{id}/optimal")
    public GameState optimalScore(@PathVariable UUID id) {
        return diceService.optimalScore(id);
    }

    @PostMapping("/{id}/manual")
    public GameState manualScore(@PathVariable UUID id, @RequestBody ManualChoice choice) {
        return diceService.manualScore(id, choice);
    }

    @PostMapping("/{id}/init-manual")
    public GameState initManualGame(@PathVariable UUID id) {
        return diceService.initManualGame(id);
    }

}
