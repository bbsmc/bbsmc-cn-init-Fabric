package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TabNavigationWidget.Builder.class)
public class TabNavigationWidgetBuilderMixin {

    @Shadow @Final private TabManager tabManager;
    @Shadow @Final private List<Tab> tabs;
    @Shadow private int width;

    @Inject(method = "build", at = @At("HEAD"))
    private void onBuild(CallbackInfoReturnable<TabNavigationWidget> cir) {
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        if (!(MinecraftClient.getInstance().currentScreen instanceof CreateWorldScreen screen)) {
            return;
        }

        for (Tab tab : tabs) {
            if (tab instanceof HostingTab) {
                return;
            }
        }

        tabs.add(new HostingTab(screen));
    }
}