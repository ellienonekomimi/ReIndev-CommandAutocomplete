package com.jellied.autocomplete.client.mixins;

import com.jellied.autocomplete.AutocompleteModClient;
import net.minecraft.mitask.PlayerCommandHandler;
import net.minecraft.mitask.command.Command;
import net.minecraft.mitask.utils.CommandHelperGUI;
import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.client.gui.GuiScreen;
import net.minecraft.src.client.packets.Packet252CommandList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(CommandHelperGUI.class)
public abstract class MixinCommandHelperGui {
    @Shadow private GuiScreen gui;
    @Shadow static List<String> commandsNames;
    @Shadow private static void setMPCommands(String message) {}
    @Shadow
    private static void setSPCommands(String message) {}

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(String message, boolean multiplayer, CallbackInfo ci) {
        if (message == null) {
            message = "";
        }

        message = message.toLowerCase();
        commandsNames.clear();

        // The default set methods seem to have a mixin or something that
        // adds spark commands into the commandNames list even if
        // it doesn't rlly belong

        if (multiplayer) {
            for (String command : Packet252CommandList.commands) {
                if (command.toLowerCase().startsWith(message)) {
                    commandsNames.add(command);
                }
            }
        }
        else {
            for (Command command : PlayerCommandHandler.commands) {
                if (command.getName().toLowerCase().startsWith(message)) {
                    commandsNames.add(command.getName());
                }
            }
        }

        Collections.sort(commandsNames);
        AutocompleteModClient.autocomplete(commandsNames);

        ci.cancel(); // Stops command helper gui from rendering
    }
}
