package com.jellied.autocomplete;

import net.minecraft.src.client.gui.GuiChat;

import java.util.ArrayList;
import java.util.List;

public class CommandWeatherAutocomplete implements CommandAutocomplete {
    final List<String> forecasts =  new ArrayList<>();
    final List<String> blankList = new ArrayList<>();

    public CommandWeatherAutocomplete() {
        forecasts.add("clear");
        forecasts.add("rain");
        forecasts.add("thunder");
    }

    @Override
    public List<String> getCommandSuggestions(GuiChat gui) {
        int commandArgIndex = AutocompleteModClient.getCursorArgIndex(gui.chat.text, gui.chat.cursorPosition);
        if (commandArgIndex != 1) {
            return blankList;
        }

        return forecasts;
    }
}
