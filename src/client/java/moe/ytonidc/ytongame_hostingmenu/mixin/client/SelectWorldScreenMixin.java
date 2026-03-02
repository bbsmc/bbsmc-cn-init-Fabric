package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.HostingScreen;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {

    @Shadow private TextFieldWidget searchBox;

    protected SelectWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        if (searchBox != null) {
            int buttonWidth = 60;
            int buttonX = searchBox.x + searchBox.getWidth() + 4;
            int buttonY = searchBox.y;
            int buttonHeight = searchBox.getHeight();

            ButtonWidget hostingButton = new ButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight,
                Text.literal("联机开服(广告)"),
                button -> this.client.setScreen(new HostingScreen(this)));

            this.addDrawableChild(hostingButton);
        }
    }
}
