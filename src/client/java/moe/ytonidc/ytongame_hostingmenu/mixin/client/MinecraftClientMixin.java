package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.Ytongame_hostingmenuClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen onSetScreen(Screen screen) {
        if (screen instanceof SelectWorldScreen || screen instanceof MultiplayerScreen) {
            Screen redirect = Ytongame_hostingmenuClient.interceptScreen();
            if (redirect != null) {
                return redirect;
            }
        }
        return screen;
    }
}
