package moe.ytonidc.ytongame_hostingmenu.mixin.client;

import moe.ytonidc.ytongame_hostingmenu.client.Config;
import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import moe.ytonidc.ytongame_hostingmenu.mixin.client.accessor.TabNavigationWidgetAccessor;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.util.Collections;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Shadow @Final private TabManager tabManager;

    @Unique
    private static final Text SUBSCRIBE_TEXT = Text.literal("订阅服务器");

    @Unique
    private Text ytongame$originalCreateButtonText = null;

    @Unique
    private ButtonWidget ytongame$findCreateButton() {
        for (var child : this.children()) {
            if (child instanceof ButtonWidget button) {
                String msg = button.getMessage().getString();
                if (msg.contains("创建") || msg.contains("Create") || msg.equals("订阅服务器")) {
                    return button;
                }
            }
        }
        return null;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (HostingTab.shouldOpenHostingTab) {
            HostingTab.shouldOpenHostingTab = false;
            for (var tab : ytongame$getAllTabs()) {
                if (tab instanceof HostingTab) {
                    tabManager.setCurrentTab(tab, true);
                    break;
                }
            }
        }
    }

    @Unique
    private Iterable<Tab> ytongame$getAllTabs() {
        for (var child : this.children()) {
            if (child instanceof TabNavigationWidget navBar) {
                return ((TabNavigationWidgetAccessor) navBar).getTabs();
            }
        }
        return Collections.emptyList();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Tab currentTab = tabManager.getCurrentTab();

        ButtonWidget createButton = ytongame$findCreateButton();
        if (createButton != null) {
            if (currentTab instanceof HostingTab) {
                if (ytongame$originalCreateButtonText == null) {
                    ytongame$originalCreateButtonText = createButton.getMessage();
                }
                if (!createButton.getMessage().getString().equals("订阅服务器")) {
                    createButton.setMessage(SUBSCRIBE_TEXT);
                }
            } else {
                if (ytongame$originalCreateButtonText != null &&
                    createButton.getMessage().getString().equals("订阅服务器")) {
                    createButton.setMessage(ytongame$originalCreateButtonText);
                }
            }
        }

        if (currentTab instanceof HostingTab hostingTab) {
            hostingTab.render(context, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        // 先让父类处理点击事件（包括取消按钮等原生控件）
        boolean handled = super.mouseClicked(click, doubled);
        if (handled) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            ButtonWidget createButton = ytongame$findCreateButton();
            if (createButton != null && button == 0) {
                if (mouseX >= createButton.getX() && mouseX <= createButton.getX() + createButton.getWidth() &&
                    mouseY >= createButton.getY() && mouseY <= createButton.getY() + createButton.getHeight()) {
                    ytongame$openPurchaseLink();
                    return true;
                }
            }

            var list = hostingTab.getPackageList();
            if (list != null && list.mouseClicked(click, doubled)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private void ytongame$openPurchaseLink() {
        try {
            Util.getOperatingSystem().open(new URI(Config.getPurchaseUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseReleased(Click click) {
        // 先让父类处理
        boolean handled = super.mouseReleased(click);
        if (handled) {
            return true;
        }

        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseReleased(click)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        // 先让父类处理
        boolean handled = super.mouseDragged(click, deltaX, deltaY);
        if (handled) {
            return true;
        }

        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseDragged(click, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }
}