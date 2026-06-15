package org.games.diceapp.controller;

import org.games.diceapp.model.Category;
import org.games.diceapp.model.GameState;
import org.games.diceapp.model.ManualChoice;
import org.games.diceapp.service.DiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dice")
public class DiceController {

    @Autowired
    private DiceService diceService;

    @PostMapping("/{id}/roll")
    public GameState rollDice(@PathVariable UUID id) {
        return diceService.rollDice(id);
    }

    @PostMapping("/roll")
    public GameState rollDice() {
        return diceService.rollDice(null);
    }

    @PostMapping("/{id}/score")
    public GameState scoreCategory(@PathVariable UUID id, @RequestBody Category category) {
        return diceService.scoreCategory(id, category);
    }

    @PostMapping("/new-game")
    public GameState newGame() {
        return diceService.newGame();
    }

    @PostMapping("/{id}/optimal")
    public GameState optimalScore(@PathVariable UUID id) {
        return diceService.optimalScore(id);
    }

    @PostMapping("/{id}/manual")
    public GameState manualScore(@PathVariable UUID id, @RequestBody ManualChoice choice) {
        return diceService.manualScore(id, choice);
    }

}
