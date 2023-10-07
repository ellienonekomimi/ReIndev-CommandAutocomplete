package com.jellied.autocomplete;

import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.game.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class CommandEffectAutocomplete {
    final List<String> blankList = new ArrayList<>();

    public List<String> getEffectsThatStartWith(String with) {
        List<String> effects = new ArrayList<>();

        for (int i = 0; i < Effect.effectlist.length - 1; i++) {
            Effect effect = Effect.effectlist[i];
            if (effect == null) {
                continue;
            }

            if (effect.name.startsWith(with.toLowerCase())) {
                effects.add(effect.name);
            }
        }

        return effects;
    }

    public List<String> getCommandSuggestions(GuiChat gui, int commandArgIndex) {
        if (commandArgIndex != 1) {
            return blankList;
        }

        String typedEffect = gui.chat.text.replaceFirst("/effect ", "");

        return getEffectsThatStartWith(typedEffect);
    }
}
