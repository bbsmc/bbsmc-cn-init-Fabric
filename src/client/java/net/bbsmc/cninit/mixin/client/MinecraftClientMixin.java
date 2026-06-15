package net.bbsmc.cninit.mixin.client;

import net.bbsmc.cninit.client.BbsmcCnInitClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen onSetScreen(Screen screen) {
        if (screen instanceof SelectWorldScreen || screen instanceof JoinMultiplayerScreen) {
            Screen redirect = BbsmcCnInitClient.interceptScreen();
            if (redirect != null) {
                return redirect;
            }
        }
        return screen;
    }
}
