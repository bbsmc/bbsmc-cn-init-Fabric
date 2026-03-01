package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends Screen {

    private static final String TITLE_KEY = "ytongame_hostingmenu.notice.title";
    private static final String[] NOTICE_KEYS = {
        "ytongame_hostingmenu.notice.intro",
        "",
        "ytongame_hostingmenu.notice.section1_title",
        "ytongame_hostingmenu.notice.section1_line1",
        "ytongame_hostingmenu.notice.section1_line2",
        "",
        "ytongame_hostingmenu.notice.section2_title",
        "ytongame_hostingmenu.notice.section2_line1",
        ""
    };

    private final JsonObject modpackJson;
    private final List<String> languagePacks;
    private final File configFile;

    private final List<OrderedText> wrappedLines = new ArrayList<>();

    public LocalizationNoticeScreen(JsonObject modpackJson, List<String> languagePacks, File configFile) {
        super(new LiteralText(I18n.translate(TITLE_KEY)));
        this.modpackJson = modpackJson;
        this.languagePacks = languagePacks;
        this.configFile = configFile;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int gap = 10;
        int totalWidth = buttonWidth * 2 + gap;
        int startX = (this.width - totalWidth) / 2;
        int buttonY = this.height - 40;

        this.addDrawableChild(new ButtonWidget(startX, buttonY, buttonWidth, buttonHeight,
                new LiteralText(I18n.translate("ytongame_hostingmenu.notice.agree")),
                btn -> onAgree()));
        this.addDrawableChild(new ButtonWidget(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight,
                new LiteralText(I18n.translate("ytongame_hostingmenu.notice.decline")),
                btn -> onDecline()));

        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String key : NOTICE_KEYS) {
            if (key.isEmpty()) {
                wrappedLines.add(OrderedText.EMPTY);
            } else {
                String line = I18n.translate(key);
                wrappedLines.addAll(this.textRenderer.wrapLines(new LiteralText(line), maxWidth));
            }
        }
    }

    private void onAgree() {
        try {
            modpackJson.addProperty("user_agreement", true);
            Ytongame_hostingmenuClient.writeJsonToFile(configFile, modpackJson);
            Ytongame_hostingmenuClient.LOGGER.info("User agreed to localization notice, user_agreement set to true");
        } catch (Exception e) {
            Ytongame_hostingmenuClient.LOGGER.error("Failed to write modpack_info.json", e);
        }

        Ytongame_hostingmenuClient.markAgreed();
        Ytongame_hostingmenuClient.setupLanguageAndPacks(this.client, languagePacks);
        this.client.setScreen(new TitleScreen());
    }

    private void onDecline() {
        Ytongame_hostingmenuClient.LOGGER.info("User declined localization notice, shutting down");
        this.client.scheduleStop();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        int textX = 30;
        int textY = 40;
        int lineHeight = 11;

        for (OrderedText line : wrappedLines) {
            if (line != OrderedText.EMPTY) {
                this.textRenderer.drawWithShadow(matrices, line, textX, textY, 0xDDDDDD);
            }
            textY += lineHeight;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            onDecline();
            return true;
        }
        return false;
    }

}
