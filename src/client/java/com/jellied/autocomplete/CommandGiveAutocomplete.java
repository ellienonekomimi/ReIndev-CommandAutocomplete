package com.jellied.autocomplete;

import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.game.item.Item;

import java.util.ArrayList;
import java.util.List;

public class CommandGiveAutocomplete {
    final List<String> blankList = new ArrayList<>();

    List<String> getItemsThatStartWith(String with) {
        List<String> items = new ArrayList<>();

        for(Item item : Item.itemsList) {
            String itemName = item.getItemName().replaceFirst("tile.", "").replaceFirst("item.", "");
            if (itemName.startsWith(with) && !items.contains(itemName)) {
                items.add(itemName);
            }
        }

        return items;
    }

    public List<String> getCommandSuggestions(GuiChat gui) {
        int commandArgIndex = AutocompleteModClient.getCursorArgIndex(gui.chat.text, gui.chat.cursorPosition);
        if (commandArgIndex != 1) {
            return blankList;
        }

        String typedItem = gui.chat.text.replaceFirst("/give ", "");

        return getItemsThatStartWith(typedItem);
    }
}
