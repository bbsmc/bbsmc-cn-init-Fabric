package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.net.URI;

public class HostingScreen extends Screen {
    private static final Text TITLE = new LiteralText("联机开服(广告)");
    private static final Text SUBSCRIBE_TEXT = new LiteralText("订阅服务器");

    private final Screen lastScreen;
    private HostingPackageList packageList;
    private ButtonWidget subscribeButton;

    public HostingScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        int listY0 = 32;
        int listY1 = this.height - 32;

        this.packageList = new HostingPackageList(
            this.client,
            this.width,
            this.height - 64,
            listY0,
            listY1,
            52
        );

        int buttonWidth = 80;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 50;
        int buttonY = 8;

        this.subscribeButton = new ButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight,
            SUBSCRIBE_TEXT,
            button -> openPurchaseLink());

        this.addDrawableChild(subscribeButton);
    }

    private void openPurchaseLink() {
        try {
            Util.getOperatingSystem().open(new URI(Config.getPurchaseUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        if (packageList != null) {
            packageList.render(matrices, mouseX, mouseY, delta);
        }

        int footerTop = this.height - 32;
        fill(matrices, 0, footerTop, this.width, this.height, 0xC0101010);

        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);

        var font = MinecraftClient.getInstance().textRenderer;
        String footerText = "* 致力为您提供稳定、流畅、24小时不断联的服务器，打造更优、更稳、更好的游戏体验！无人值守也可玩！";
        int footerY = this.height - 20;

        int textWidth = font.getWidth(footerText);
        int startX = (this.width - textWidth) / 2;

        int charX = startX;
        int textLength = footerText.length();
        for (int i = 0; i < textLength; i++) {
            String ch = String.valueOf(footerText.charAt(i));

            float progress = (float) i / (textLength - 1);
            int color = getGradientColor(progress);

            font.draw(matrices, ch, charX, footerY, color);
            charX += font.getWidth(ch);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (packageList != null && packageList.mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (packageList != null && packageList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (packageList != null && packageList.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (packageList != null && packageList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void close() {
        this.client.setScreen(lastScreen);
    }
}
