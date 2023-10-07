package com.jellied.autocomplete;

import com.fox2code.foxloader.loader.ClientMod;
import com.fox2code.foxloader.loader.ModLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.src.client.gui.Gui;
import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.client.gui.ScaledResolution;
import org.lwjgl.Sys;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteModClient extends AutocompleteMod implements ClientMod {
    // Constants
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ScaledResolution res = ScaledResolution.instance;

    private static final int offsetFromChatBar = 22 - 4 - 1;

    public static final Map<String, Object> commandSpecificAutocompletion = new HashMap<>();
    public static Map<String, Method> getSuggestionMethods = new HashMap<>();

    // For cycling thru commands with tab
    private static int currentSuggestionCycleIndex = 0;
    private static String currentChatStringListeningTo = null;

    private static int suggestionBeginIndex = 0;

    private static String currentlySelectedSuggestion = "";

    // Colors
    private static final int BLACK = new Color(0, 0, 0, (int) (255 * 0.75)).getRGB();



    // chewsday init
    public void onPreInit() {
        addAutocomplete("/give", new CommandGiveAutocomplete());
        addAutocomplete("/weather", new CommandWeatherAutocomplete());
        addAutocomplete("/effect", new CommandEffectAutocomplete());
        addAutocomplete("/time", new CommandTimeAutocomplete());

        InputHandler.initKeys();
    }

    public void addAutocomplete(String command, Object commandAutocomplete) {
        commandSpecificAutocompletion.put(command, commandAutocomplete);
    }



    // Input-related methods
    public static void cycleSuggestionIndex(List<String> suggestions, int direction) {
        int nextCycleIndex = currentSuggestionCycleIndex + direction;

        if (nextCycleIndex < 0) {
            currentSuggestionCycleIndex = suggestions.size() - 1;
            suggestionBeginIndex = (suggestions.size() - 1) - Math.min(7, suggestions.size() - 1);

            // array index out of bounds exception ^^^
            // just kidding i fixed it

            return;
        }

        if (nextCycleIndex >= suggestions.size()) {
            currentSuggestionCycleIndex = 0;
            suggestionBeginIndex = 0;

            return;
        }

        int minIndexBound = suggestionBeginIndex;
        int maxIndexBound = suggestionBeginIndex + 7;

        if (nextCycleIndex < minIndexBound) {
            suggestionBeginIndex--;
        }
        else if (nextCycleIndex > maxIndexBound) {
            suggestionBeginIndex++;
        }

        currentSuggestionCycleIndex = nextCycleIndex;
    }

    public static void autocompleteCurrentSuggestion() {
        GuiChat gui = (GuiChat) minecraft.currentScreen;

        int argIndex = 0;
        int substringEnd = 0;

        for (int i = 0; i <= gui.chat.text.length() - 1; i++) {
            substringEnd++;

            if (gui.chat.text.charAt(i) == ' ') {
                argIndex++;
            }

            if (argIndex == getCursorArgIndex(gui.chat.text, gui.chat.cursorPosition)) {
                break;
            }
        }

        gui.chat.setText(gui.chat.text.substring(0, substringEnd) + currentlySelectedSuggestion + " ");
        gui.chat.setCursorPosition(gui.chat.text.length());
    }



    // Gui-related methods
    public static int getCursorArgIndex(String command, int cursorPos) {
        int argIndex = 0;

        for (int i = 0; i <= command.length() - 1; i++) {
            if (i == cursorPos) {
                break;
            }

            if (command.charAt(i) == ' ') {
                argIndex++;
            }
        }

        return argIndex;
    }

    public static int getXPadding(String typedCommand, int cursorPosition) {
        int padding = minecraft.fontRenderer.getStringWidth("> /") + 4;

        String[] commandArgs = typedCommand.split(" ");
        int argIndex = getCursorArgIndex("/" + typedCommand, cursorPosition);

        for (int i = 0; i <= commandArgs.length - 1; i++) {
            if (i == argIndex) {
                break;
            }

            padding += minecraft.fontRenderer.getStringWidth(commandArgs[i] + " ");
        }

        return padding;
    }

    public static int getMaxSuggestionWidth(List<String> suggestions) {
        int thiccness = 0;
        int totalSuggestions = suggestions.size() - 1;

        for (int i = suggestionBeginIndex; i <= Math.min(totalSuggestions, suggestionBeginIndex + totalSuggestions); i++) {
            thiccness = Math.max(minecraft.fontRenderer.getStringWidth(suggestions.get(i)), thiccness);
        }

        return thiccness;
    }

    public static void drawSuggestions(GuiChat gui, List<String> suggestions) {
        if (suggestions.isEmpty()) {
            return;
        }

        String typedSuggestion = gui.chat.text.substring(1); // without the '/'
        InputHandler.detectInput(suggestions);

        if (currentChatStringListeningTo != null && !currentChatStringListeningTo.equals(typedSuggestion)) {
            currentSuggestionCycleIndex = 0; // Reset the tab cycle
            suggestionBeginIndex = 0;
        }

        int suggestionAmount = Math.min(suggestions.size() - 1, 7);

        int xPosition = getXPadding(typedSuggestion, gui.chat.cursorPosition); // The start of the current "chunk" of text
        int yPosition = res.getScaledHeight() - (offsetFromChatBar); // Just above the chat bar

        int backgroundSizeX = getMaxSuggestionWidth(suggestions) + 6;
        int backgroundSizeY = (suggestionAmount + 1) * 12;

        Gui.drawRect(xPosition - 2, yPosition, xPosition + backgroundSizeX, yPosition - backgroundSizeY - 2, BLACK);

        int drawAtY = yPosition - 12; // Start from the bottom
        for (int i = suggestionBeginIndex + suggestionAmount; i >= suggestionBeginIndex; i--) {
            String suggestion = suggestions.get(i);
            int textColor = i == currentSuggestionCycleIndex ? Color.YELLOW.getRGB() : Color.WHITE.getRGB();

            currentlySelectedSuggestion = i == currentSuggestionCycleIndex ? suggestion : currentlySelectedSuggestion;

            minecraft.fontRenderer.drawStringWithShadow(suggestion, xPosition, drawAtY, textColor);
            drawAtY -= 12; // Work our way up
        }

        currentChatStringListeningTo = typedSuggestion;
    }

    @SuppressWarnings("unchecked")
    public static void autocomplete(List<String> commands) {
        if (minecraft.currentScreen == null | !(minecraft.currentScreen instanceof GuiChat)) {
            return;
        }

        GuiChat gui = (GuiChat) minecraft.currentScreen;
        String[] commandArgs = gui.chat.text.split(" ");
        int commandArgIndex = getCursorArgIndex(gui.chat.text, gui.chat.cursorPosition);
        Object cmd = commandSpecificAutocompletion.get(commandArgs[0]);

        if (commandArgIndex == 0) {
            drawSuggestions(gui, commands);
            return;
        }

        if (cmd == null) {
            return;
        }

        String cmdClassName = cmd.getClass().getName();
        if (getSuggestionMethods.get(cmdClassName) != null) {
            try {
                List<String> suggestions = (List<String>) getSuggestionMethods.get(cmdClassName).invoke(cmd, gui, commandArgIndex);
                drawSuggestions(gui, suggestions);
            }
            catch (Exception e) {
                System.out.println("Exception when attempting to call getCommandSuggestions on class " + cmd.getClass() + ": ");
                e.printStackTrace();
            }
        }
        else {
            try {
                Method getSuggestions = cmd.getClass().getMethod("getCommandSuggestions", GuiChat.class, int.class);
                getSuggestionMethods.put(cmdClassName, getSuggestions);
            }
            catch (Exception e) {
                System.out.println("Exception when attempting to get getCommandSuggestions from class " + cmd.getClass() + ": ");
                e.printStackTrace();
            }
        }
    }
}
