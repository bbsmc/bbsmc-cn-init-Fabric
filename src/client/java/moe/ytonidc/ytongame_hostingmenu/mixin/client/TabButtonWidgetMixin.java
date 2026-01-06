package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TabButtonWidget.class)
public abstract class TabButtonWidgetMixin extends ClickableWidget {

    @Shadow @Final private TabManager tabManager;
    @Shadow @Final private Tab tab;

    protected TabButtonWidgetMixin() {
        super(0, 0, 0, 0, null);
    }

    @Inject(method = "isCurrentTab", at = @At("HEAD"), cancellable = true)
    private void onIsCurrentTab(CallbackInfoReturnable<Boolean> cir) {
        Tab currentTab = tabManager.getCurrentTab();
        if (this.tab instanceof HostingTab && currentTab instanceof HostingTab) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void onRenderWidget(DrawContext context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Tab currentTab = tabManager.getCurrentTab();
        if (this.tab instanceof HostingTab && currentTab instanceof HostingTab) {
            context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFFFFFF);
        }
    }
}