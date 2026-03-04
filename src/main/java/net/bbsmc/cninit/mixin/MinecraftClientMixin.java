package net.bbsmc.cninit.mixin;

import net.bbsmc.cninit.client.BbsmcCnInitClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @ModifyVariable(method = "openScreen", at = @At("HEAD"), argsOnly = true)
    private Screen onOpenScreen(Screen screen) {
        if (screen instanceof SelectWorldScreen || screen instanceof MultiplayerScreen) {
            Screen redirect = BbsmcCnInitClient.interceptScreen();
            if (redirect != null) {
                return redirect;
            }
        }
        return screen;
    }
}
