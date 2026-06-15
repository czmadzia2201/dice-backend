package org.games.diceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.games.diceapp.model.Category;
import org.games.diceapp.model.GamePhase;
import org.games.diceapp.model.GameState;
import org.games.diceapp.service.DiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static java.lang.String.format;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiceController.class)
class DiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiceService diceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRollDice() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = UUID.randomUUID();
        when(diceService.rollDice(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/roll", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRoll").value(new ArrayList<>()))
                .andExpect(jsonPath("$.gamePhase").value(GamePhase.ROLL.name()));
    }

    @Test
    void shouldRollDiceWithoutId() throws Exception {
        GameState gameState = new GameState();

        when(diceService.rollDice(null)).thenReturn(gameState);

        mockMvc.perform(post("/dice/roll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRoll").value(new ArrayList<>()))
                .andExpect(jsonPath("$.gamePhase").value(GamePhase.ROLL.name()));
    }

    @Test
    void shouldStartNewGame() throws Exception {
        GameState gameState = new GameState();

        when(diceService.newGame()).thenReturn(gameState);

        mockMvc.perform(post("/dice/new-game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRoll").value(new ArrayList<>()))
                .andExpect(jsonPath("$.gamePhase").value(GamePhase.ROLL.name()));
    }

    @Test
    void shouldScoreCategory() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = UUID.randomUUID();
        when(diceService.scoreCategory(gameId, Category.SIXES))
                .thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/score", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Category.SIXES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRoll").value(new ArrayList<>()))
                .andExpect(jsonPath("$.gamePhase").value(GamePhase.ROLL.name()));

    }

    @Test
    void shouldReturnOptimalScore() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = UUID.randomUUID();
        when(diceService.optimalScore(gameId))
                .thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/optimal", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRoll").value(new ArrayList<>()))
                .andExpect(jsonPath("$.gamePhase").value(GamePhase.ROLL.name()));
    }

}