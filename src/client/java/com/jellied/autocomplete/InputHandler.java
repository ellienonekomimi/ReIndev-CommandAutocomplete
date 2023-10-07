package com.jellied.autocomplete;

import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputHandler {
    static Map<Integer, Boolean> keyStates = new HashMap<>();

    public static void initKeys() {
        keyStates.put(Keyboard.KEY_UP, true);
        keyStates.put(Keyboard.KEY_DOWN, true);
        keyStates.put(Keyboard.KEY_TAB, true);
    }

    public static void detectInput(List<String> suggestions) {
        int cycleDirection = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_UP) && keyStates.get(Keyboard.KEY_UP)) {
            cycleDirection++;
            keyStates.put(Keyboard.KEY_UP, false);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            keyStates.put(Keyboard.KEY_UP, true);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && keyStates.get(Keyboard.KEY_DOWN)) {
            cycleDirection--;
            keyStates.put(Keyboard.KEY_DOWN, false);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            keyStates.put(Keyboard.KEY_DOWN, true);
        }

        AutocompleteModClient.cycleSuggestionIndex(suggestions, -cycleDirection);

        if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && keyStates.get(Keyboard.KEY_TAB)) {
            AutocompleteModClient.autocompleteCurrentSuggestion();
            keyStates.put(Keyboard.KEY_TAB, false);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
            keyStates.put(Keyboard.KEY_TAB, true);
        }
    }
}
