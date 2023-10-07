package com.jellied.autocomplete;

import net.minecraft.src.client.gui.GuiChat;

import java.util.List;

public interface CommandAutocomplete {
    List<String> getCommandSuggestions(GuiChat gui);
}
