package com.jellied.autocomplete;

import net.minecraft.src.client.gui.GuiChat;

import java.util.ArrayList;
import java.util.List;

public class CommandWeatherAutocomplete {
    final List<String> forecasts =  new ArrayList<>();
    final List<String> blankList = new ArrayList<>();

    public CommandWeatherAutocomplete() {
        forecasts.add("clear");
        forecasts.add("rain");
        forecasts.add("thunder");
    }

    public List<String> getCommandSuggestions(GuiChat gui, int commandArgIndex) {
        if (commandArgIndex != 1) {
            return blankList;
        }

        return forecasts;
    }
}
