package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.HostingScreen;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private ButtonWidget ytongame$hostingButton;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        int buttonWidth = 60;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 80;
        int buttonY = 8;

        ytongame$hostingButton = new ButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight,
            Text.literal("联机开服(广告)"),
            button -> this.client.setScreen(new HostingScreen(this)));

        this.addDrawableChild(ytongame$hostingButton);
    }
}
