package com.jellied.autocomplete;

import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputHandler {
    static Map<Integer, Boolean> keyStates = new HashMap<>();
    static long timeArrowsHeldDown = 0;
    static int heldCycleSpeed = 500;

    static long lastCycle = 0;

    public static void initKeys() {
        keyStates.put(Keyboard.KEY_TAB, true);
    }

    public static void detectInput(List<String> suggestions) {
        // Arrow keys
        int cycleDirection = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            cycleDirection++;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            cycleDirection--;
        }

        if (cycleDirection == 0) {
            timeArrowsHeldDown = System.currentTimeMillis();
            lastCycle = timeArrowsHeldDown;
        }

        long timeHeld = System.currentTimeMillis() - timeArrowsHeldDown;
        if (timeHeld >= 3000) {
            heldCycleSpeed = 40;
        }
        else if (timeHeld >= 1750) {
            heldCycleSpeed = 100;
        }
        else if (timeHeld >= 1000) {
            heldCycleSpeed = 150;
        }
        else {
            heldCycleSpeed = 250;
        }

        long timeSinceLastCycle = System.currentTimeMillis() - lastCycle;
        if (cycleDirection != 0 && (timeSinceLastCycle >= heldCycleSpeed | lastCycle == timeArrowsHeldDown)) {
            lastCycle = System.currentTimeMillis();
            AutocompleteModClient.cycleSuggestionIndex(suggestions, -cycleDirection);
        }

        // Tab
        if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && keyStates.get(Keyboard.KEY_TAB)) {
            AutocompleteModClient.autocompleteCurrentSuggestion();
            keyStates.put(Keyboard.KEY_TAB, false);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
            keyStates.put(Keyboard.KEY_TAB, true);
        }
    }
}
