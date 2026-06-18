package org.games.diceapp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.games.diceapp.model.RollEntry;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiceUtils {

    public static List<RollEntry> generateEmptyManualHistory() {
        List<RollEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            entries.add(new RollEntry(i, List.of(), null, null));
        }
        return entries;
    }

}
