package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class HostingTab implements Tab {
    private static final Text TITLE = Text.literal("联机开服");

    public static boolean shouldOpenHostingTab = false;

    private final CreateWorldScreen screen;
    private HostingPackageList packageList;
    private ScreenRect tabArea;

    public HostingTab(CreateWorldScreen screen) {
        this.screen = screen;
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
    }

    @Override
    public void refreshGrid(ScreenRect rectangle) {
        this.tabArea = rectangle;

        MinecraftClient minecraft = MinecraftClient.getInstance();
        int listTop = rectangle.getTop();

        this.packageList = new HostingPackageList(minecraft, rectangle.width(), rectangle.height() - 25, listTop, 52);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTick) {
        if (packageList != null) {
            packageList.render(context, mouseX, mouseY, partialTick);
        }

        if (tabArea != null) {
            var font = MinecraftClient.getInstance().textRenderer;
            String footerText = "* 致力为您提供稳定、流畅、24小时不断联的服务器，打造更优、更稳、更好的游戏体验！无人值守也可玩！";
            int footerY = tabArea.getTop() + tabArea.height() - 16;

            int textWidth = font.getWidth(footerText);
            int startX = tabArea.getLeft() + (tabArea.width() - textWidth) / 2;

            int charX = startX;
            int textLength = footerText.length();
            for (int i = 0; i < textLength; i++) {
                String ch = String.valueOf(footerText.charAt(i));

                float progress = (float) i / (textLength - 1);
                int color = getGradientColor(progress);

                context.drawText(font, ch, charX, footerY, color, false);
                charX += font.getWidth(ch);
            }
        }
    }

    private int getGradientColor(float progress) {
        int startR = 0xAA, startG = 0xFF, startB = 0x55;
        int midR = 0x00, midG = 0xFF, midB = 0x88;
        int endR = 0x00, endG = 0xAA, endB = 0xCC;

        int r, g, b;
        if (progress < 0.5f) {
            float t = progress * 2;
            r = (int) (startR + (midR - startR) * t);
            g = (int) (startG + (midG - startG) * t);
            b = (int) (startB + (midB - startB) * t);
        } else {
            float t = (progress - 0.5f) * 2;
            r = (int) (midR + (endR - midR) * t);
            g = (int) (midG + (endG - midG) * t);
            b = (int) (midB + (endB - midB) * t);
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public HostingPackageList getPackageList() {
        return packageList;
    }
}