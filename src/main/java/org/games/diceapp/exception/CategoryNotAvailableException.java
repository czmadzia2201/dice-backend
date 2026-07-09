package org.games.diceapp.exception;

import org.games.diceapp.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryNotAvailableException extends RuntimeException {
    public CategoryNotAvailableException(UUID gameId, Category category) {
        super(String.format("Category %s is not available for game %s", category, gameId));
    }
}
