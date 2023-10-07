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

    public List<String> getEntriesThatBeginWith(List<String> listToCheck, String with) {
        List<String> entries = new ArrayList<>();

        for (String entry : listToCheck) {
            if (entry != null && entry.startsWith(with)) {
                entries.add(entry);
            }
        }

        return entries;
    }

    public List<String> getCommandSuggestions(GuiChat gui, int commandArgIndex) {
        if (commandArgIndex != 1) {
            return blankList;
        }

        String typedWeather = gui.chat.text.replaceFirst("/weather ", "");
        return getEntriesThatBeginWith(forecasts, typedWeather);
    }
}
