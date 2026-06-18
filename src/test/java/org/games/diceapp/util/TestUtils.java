package org.games.diceapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.games.diceapp.model.GameState;

import java.net.URL;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    public static GameState loadGameStateFromJson(String fileName) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        URL resource = TestUtils.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(format("File %s not found", fileName));
        }
        return objectMapper.readValue(resource, GameState.class);
    }

}
