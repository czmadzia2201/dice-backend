package org.games.diceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.games.diceapp.exception.GameNotFoundException;
import org.games.diceapp.model.*;
import org.games.diceapp.service.DiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verifyNoInteractions;
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
    void newGameEndpoint_shouldReturnGameState() throws Exception {
        GameState gameState = new GameState();

        when(diceService.newGame(RollMode.THREE_ROLLS)).thenReturn(gameState);

        mockMvc.perform(post("/dice/new-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RollMode.THREE_ROLLS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void newGameEndpoint_invalidBody_shouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/dice/new-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("BANANA")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void newGameEndpoint_nullBody_shouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/dice/new-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void newGameAndRollDiceEndpoint_shouldReturnGameState() throws Exception {
        GameState gameState = new GameState();

        when(diceService.newGameAndRollDice(RollMode.SINGLE_ROLL)).thenReturn(gameState);

        mockMvc.perform(post("/dice/roll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RollMode.SINGLE_ROLL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void newGameAndRollDiceEndpoint_invalidBody_shouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/dice/roll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("BANANA")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void newGameAndRollDiceEndpoint_nullBody_shouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/dice/roll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void rollDiceEndpoint_shouldReturnGameState() throws Exception {
        GameState gameState = new GameState();
        UUID gameId = gameState.getId();
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);

        when(diceService.rollDice(gameId, diceToRoll)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/roll", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diceToRoll)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void rollDiceEndpoint_gameIdNotFound_shouldReturnNotFoundStatus() throws Exception{
        UUID gameId = UUID.randomUUID();
        List<Boolean> diceToRoll = List.of(true, true, true, true, true);

        when(diceService.rollDice(gameId, diceToRoll)).thenThrow(new GameNotFoundException(gameId));

        mockMvc.perform(post(format("/dice/%s/roll", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diceToRoll)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    assertEquals("Game not found: " + gameId,
                            result.getResolvedException().getMessage()
                    );
                });
    }

    @Test
    void rollDiceEndpoint_invalidBodySize_shouldReturnBadRequestStatus() throws Exception{
        GameState gameState = new GameState();
        UUID gameId = gameState.getId();
        List<Boolean> diceToRoll = List.of(true, true, true, true);

        when(diceService.rollDice(gameId, diceToRoll))
                .thenThrow(new IllegalArgumentException("DiceToRoll must contain exactly 5 values."));

        mockMvc.perform(post(format("/dice/%s/roll", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diceToRoll)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    assertEquals("DiceToRoll must contain exactly 5 values.",
                            result.getResolvedException().getMessage()
                    );
                });
    }

    @Test
    void rollDiceEndpoint_invalidBody_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/roll", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(true, true, true, "abc", true))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void rollDiceEndpoint_nullBody_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/roll", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void scoreCategoryEndpoint_shouldReturnGameState() throws Exception {
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
    void scoreCategoryEndpoint_wrongGameState_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();
        UUID gameId = gameState.getId();

        when(diceService.scoreCategory(gameId, Category.SIXES))
                .thenThrow(new IllegalStateException("Action not allowed at this state of the game."));

        mockMvc.perform(post(format("/dice/%s/score", gameId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Category.SIXES)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    assertEquals("Action not allowed at this state of the game.",
                            result.getResolvedException().getMessage()
                    );
                });
    }

    @Test
    void scoreCategoryEndpoint_invalidBody_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/score", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("BANANA")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void scoreCategoryEndpoint_nullBody_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/score", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    @Test
    void optimalEndpoint_shouldReturnGameState() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.optimalScore(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/optimal", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void initManualEndpoint_shouldReturnGameState() throws Exception {
        GameState gameState = new GameState();

        UUID gameId = gameState.getId();
        when(diceService.initManualGame(gameId)).thenReturn(gameState);

        mockMvc.perform(post(format("/dice/%s/init-manual", gameId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()));
    }

    @Test
    void manualEndpoint_shouldReturnGameState() throws Exception {
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

    @ParameterizedTest
    @MethodSource("manualChoices")
    void manualEndpoint_invalidBody_shouldReturnBadRequestStatus(ManualChoice choice) throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/manual", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(choice)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

    private static Stream<Arguments> manualChoices() {
        return Stream.of(
                Arguments.of(new ManualChoice(15, Category.SIXES, ChoiceAction.ASSIGN)),
                Arguments.of(new ManualChoice(-1, Category.SIXES, ChoiceAction.ASSIGN)),
                Arguments.of(new ManualChoice(1, null, ChoiceAction.ASSIGN)),
                Arguments.of(new ManualChoice(1, Category.SIXES, null))
        );
    }

    @Test
    void manualEndpoint_nullBody_shouldReturnBadRequestStatus() throws Exception {
        GameState gameState = new GameState();

        mockMvc.perform(post(format("/dice/%s/manual", gameState.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(diceService);
    }

}