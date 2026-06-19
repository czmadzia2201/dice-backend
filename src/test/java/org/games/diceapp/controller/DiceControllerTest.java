package org.games.diceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.games.diceapp.exception.GameNotFoundException;
import org.games.diceapp.model.*;
import org.games.diceapp.service.DiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldReturnGameState_rollDiceEndpoint() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.rollDice(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/roll", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));

    }

    @Test
    void shouldReturnGameState_rollDiceEndpoint_noGameId() throws Exception {
        GameState gameState = new GameState();

        when(diceService.rollDice(null)).thenReturn(gameState);

        mockMvc.perform(post("/dice/roll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldReturnNotFoundStatus_gameIdNotFound() throws Exception{
        UUID gameId = UUID.randomUUID();
        when(diceService.rollDice(gameId)).thenThrow(new GameNotFoundException(gameId));

        mockMvc.perform(post(format("/dice/%s/roll", gameId)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    assertEquals("Game not found: " + gameId,
                            result.getResolvedException().getMessage()
                    );
                });
    }

    @Test
    void shouldReturnGameState_newGameEndpoint() throws Exception {
        GameState gameState = new GameState();

        when(diceService.newGame()).thenReturn(gameState);

        mockMvc.perform(post("/dice/new-game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldReturnGameState_scoreCategoryEndpoint() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.scoreCategory(gameId, Category.SIXES)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/score", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Category.SIXES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void shouldReturnGameState_optimalEndpoint() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.optimalScore(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/optimal", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void shouldReturnGameState_initManualEndpoint() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.initManualGame(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/init-manual", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void shouldReturnGameState_manualEndpoint() throws Exception {
        GameState gameState = new GameState();
        ManualChoice choice = new ManualChoice(1, Category.SIXES, ChoiceAction.ASSIGN);

        UUID gameId = gameState.getId();
        when(diceService.manualScore(gameId, choice)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/manual", gameId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(choice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

}